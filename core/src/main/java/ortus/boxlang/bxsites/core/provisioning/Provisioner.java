package ortus.boxlang.bxsites.core.provisioning;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Resolves, checksum-verifies, and caches the two artifacts bx-sites tooling
 * needs at runtime, then assembles a per-project {@code BOXLANG_HOME}
 * from them - all via plain HTTP + a local cache, not either build tool's
 * native dependency-resolution engine (deliberately: this logic must run
 * identically from a Gradle task and a Maven Mojo, and Maven has no clean
 * equivalent of Gradle's "custom Ivy repository at a predictable URL" trick).
 *
 * <p>Downloaded artifacts land in a shared, cross-project cache
 * ({@link ortus.boxlang.bxsites.core.BxSitesConfig#defaultCacheDir()} by
 * default) keyed by version, so a machine with many bx-sites-enabled
 * projects only downloads each version once. Each <em>project's</em> own
 * {@code BOXLANG_HOME} is then assembled by copying/unzipping from that
 * cache, never by re-downloading.
 */
public final class Provisioner {

    private final Downloader downloader;
    private final Path cacheDir;

    public Provisioner(Downloader downloader, Path cacheDir) {
        this.downloader = downloader;
        this.cacheDir = cacheDir;
    }

    /**
     * Resolves the boxlang-miniserver jar for {@code version}, downloading
     * and checksum-verifying it into the shared cache if not already
     * present there.
     *
     * @return the cached jar's path
     */
    public Path resolveMiniserverJar(String version) {
        return resolveCached(
                cacheDir.resolve("boxlang-miniserver").resolve(version).resolve("boxlang-miniserver-" + version + ".jar"),
                ArtifactCoordinates.miniserverJarUrl(version),
                ArtifactCoordinates.miniserverJarChecksumUrl(version),
                "SHA-256");
    }

    /**
     * Resolves the {@code bx-sites-<version>-with-deps.zip} bundle,
     * downloading and checksum-verifying it into the shared cache if not
     * already present there.
     *
     * @return the cached zip's path
     */
    public Path resolveWithDepsZip(String version) {
        return resolveCached(
                cacheDir.resolve("bx-sites-with-deps").resolve(version).resolve("bx-sites-" + version + "-with-deps.zip"),
                ArtifactCoordinates.bxSitesWithDepsZipUrl(version),
                ArtifactCoordinates.bxSitesWithDepsZipChecksumUrl(version),
                "SHA-512");
    }

    /**
     * Unzips the resolved {@code -with-deps} bundle for {@code bxSitesVersion}
     * into {@code boxlangHomeDir/modules/bxsites/} as one self-contained
     * unit - the zip has no wrapper folder of its own (it <em>is</em>
     * bx-sites' own module root), so this method creates the destination
     * directory and extracts straight into it. BoxLang's module-inception
     * mechanism then activates every dependency nested under that tree's
     * own {@code modules/} folder automatically at runtime - confirmed
     * during the M0 spike, no per-dependency handling needed here.
     *
     * <p>Idempotent: if the destination already contains a
     * {@code ModuleConfig.bx} (i.e. a previous provision already ran), this
     * is a no-op, so repeated calls across a Gradle task's up-to-date
     * checks or a Maven Mojo's own re-invocation are cheap.
     *
     * @return {@code boxlangHomeDir}, for convenience chaining
     */
    public Path provisionBoxlangHome(String bxSitesVersion, Path boxlangHomeDir) {
        Path moduleDir = boxlangHomeDir.resolve("modules").resolve(ArtifactCoordinates.BXSITES_MODULE_MAPPING_NAME);
        Path versionMarker = moduleDir.resolve(".bxsites-version");

        if (Files.exists(moduleDir.resolve("ModuleConfig.bx")) && Files.exists(versionMarker)) {
            try {
                if (bxSitesVersion.equals(Files.readString(versionMarker).trim())) {
                    return boxlangHomeDir;
                }
            } catch (IOException e) {
                // Fall through to reprovision.
            }
        }

        if (Files.exists(moduleDir)) {
            try (var walk = Files.walk(moduleDir)) {
                walk.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            } catch (UncheckedIOException e) {
                throw new UncheckedIOException("Failed to clear existing " + moduleDir + " for reprovision", e.getCause());
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to clear existing " + moduleDir + " for reprovision", e);
            }
        }

        Path zip = resolveWithDepsZip(bxSitesVersion);
        try {
            Files.createDirectories(moduleDir);
            unzip(zip, moduleDir);
            Files.writeString(versionMarker, bxSitesVersion, java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to unpack " + zip + " into " + moduleDir, e);
        }
        return boxlangHomeDir;
    }

    /**
     * Runs the full provisioning flow for one config: resolves the
     * miniserver jar and assembles the project's own {@code BOXLANG_HOME}.
     */
    public ProvisionResult provision(String boxlangMiniserverVersion, String bxSitesVersion, Path boxlangHomeDir) {
        Path miniserverJar = resolveMiniserverJar(boxlangMiniserverVersion);
        Path home = provisionBoxlangHome(bxSitesVersion, boxlangHomeDir);
        return new ProvisionResult(miniserverJar, home);
    }

    // ---------------------------------------------------------------------

    private Path resolveCached(Path dest, String url, String checksumUrl, String digestAlgorithm) {
        if (Files.exists(dest)) {
            return dest;
        }
        byte[] bytes = downloader.fetch(url);
        String expected = parseChecksum(downloader.fetch(checksumUrl));
        String actual = digestHex(bytes, digestAlgorithm);
        if (!expected.equalsIgnoreCase(actual)) {
            throw new IllegalStateException(
                    "Checksum mismatch downloading " + url + ": expected " + digestAlgorithm + " " + expected
                            + " but got " + actual + ". If this is a mutable snapshot build, it may have been"
                            + " overwritten upstream between the artifact and checksum file being published -"
                            + " retrying should pick up a consistent pair.");
        }
        try {
            Files.createDirectories(dest.getParent());
            Path tmp = Files.createTempFile(dest.getParent(), dest.getFileName().toString(), ".tmp");
            Files.write(tmp, bytes);
            Files.move(tmp, dest, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to cache download from " + url + " to " + dest, e);
        }
        return dest;
    }

    /** A `sha256sum`-style checksum file: "<hex>  <filename>". */
    private static String parseChecksum(byte[] checksumFileBytes) {
        String text = new String(checksumFileBytes, java.nio.charset.StandardCharsets.UTF_8).trim();
        int firstWhitespace = text.indexOf(' ');
        return (firstWhitespace > 0 ? text.substring(0, firstWhitespace) : text).trim();
    }

    private static String digestHex(byte[] data, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(data);
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(algorithm + " not available on this JVM", e);
        }
    }

    private static void unzip(Path zipFile, Path destDir) throws IOException {
        try (InputStream fis = Files.newInputStream(zipFile);
                ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path target = destDir.resolve(entry.getName()).normalize();
                if (!target.startsWith(destDir)) {
                    throw new IOException("Zip entry escapes destination directory: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                } else {
                    if (target.getParent() != null) {
                        Files.createDirectories(target.getParent());
                    }
                    Files.copy(zis, target, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    /** The outcome of a full {@link #provision} call. */
    public record ProvisionResult(Path miniserverJar, Path boxlangHomeDir) {
    }
}
