package ortus.boxlang.bxsites.core.springboot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

/**
 * Wires a springdoc-generated OpenAPI/Swagger spec into a bx-sites project:
 * copies the spec into the content dir's {@code assets/openapi/}, writes a
 * thin Markdown page wrapping bx-sites' own {@code ::: openapi :::} content
 * block (see {@code guides/openapi.md}), and makes sure the resolved
 * config actually has {@code openapi: true} set - bx-sites renders the
 * block as an inert placeholder otherwise (confirmed against
 * {@code guides/openapi.md}: "unset, this placeholder renders but stays
 * inert").
 *
 * <p>Requires the consumer to already have springdoc's own Gradle/Maven
 * OpenAPI plugin applied (it launches the app and saves the spec file) -
 * this generator only consumes that already-generated file, never
 * generates OpenAPI content itself.
 *
 * <p><b>Known limitation, not solved here:</b> Swagger UI renders
 * entirely client-side, so per-endpoint text never reaches bx-sites' own
 * search index - only this wrapper page's title/frontmatter is indexed.
 * A fast-follow could pre-render a static per-operation summary to close
 * this gap.
 */
public final class OpenApiDocGenerator {

    private OpenApiDocGenerator() {
    }

    /**
     * @param specFile         the springdoc-generated spec file (.json/.yaml/.yml)
     * @param contentDir       the resolved bx-sites content dir (docs/ or src/)
     * @param configFile       the resolved bxsites.yaml/.yml/.toml/.json
     * @param pageTitle        title for both the generated page's frontmatter and the openapi block
     * @param pagePath         where to write the page, relative to contentDir (e.g. {@code api/openapi.md})
     * @param autoPatchConfig  when {@code openapi: true} isn't already set: patch it in (YAML/TOML only)
     *                         instead of failing
     */
    public record Request(
            Path specFile,
            Path contentDir,
            Path configFile,
            String pageTitle,
            Path pagePath,
            boolean autoPatchConfig) {
    }

    public record Result(Path copiedSpecFile, Path pageFile, String specAssetPath) {
    }

    public static Result generate(Request request) throws IOException {
        if (!Files.isRegularFile(request.specFile()) || Files.size(request.specFile()) == 0) {
            throw new IllegalStateException(
                    "No OpenAPI spec found at " + request.specFile()
                            + " - run your springdoc Gradle/Maven plugin first so it generates the spec file.");
        }

        ensureOpenApiEnabled(request);

        String extension = specExtension(request.specFile());
        Path assetsDir = request.contentDir().resolve("assets").resolve("openapi");
        Files.createDirectories(assetsDir);
        Path copiedSpec = assetsDir.resolve("openapi." + extension);
        Files.copy(request.specFile(), copiedSpec, StandardCopyOption.REPLACE_EXISTING);
        String specAssetPath = "assets/openapi/openapi." + extension;

        Path pageFile = request.contentDir().resolve(request.pagePath());
        Files.createDirectories(pageFile.getParent());
        Files.writeString(pageFile, renderPage(request.pageTitle(), specAssetPath));

        return new Result(copiedSpec, pageFile, specAssetPath);
    }

    private static void ensureOpenApiEnabled(Request request) throws IOException {
        if (OpenApiConfigFlag.isEnabled(request.configFile())) {
            return;
        }
        if (request.autoPatchConfig()) {
            OpenApiConfigFlag.enable(request.configFile());
            return;
        }
        throw new IllegalStateException(
                "openapi: true is not set in " + request.configFile()
                        + " - add it manually (see bx-sites' Configuration guide), or enable "
                        + "autoPatchConfig to have this generator do it for you (YAML/TOML configs only).");
    }

    private static String renderPage(String pageTitle, String specAssetPath) {
        String frontMatter = FrontMatter.render(new FrontMatter.Fields(pageTitle, null, null, null, List.of("api", "openapi")));
        return frontMatter
                + "\n# " + pageTitle + "\n\n"
                + "::: openapi src=\"" + specAssetPath + "\" title=\"" + pageTitle + "\"\n"
                + ":::\n";
    }

    private static String specExtension(Path specFile) {
        String name = specFile.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String ext = dot >= 0 ? name.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
        return switch (ext) {
            case "json" -> "json";
            case "yaml", "yml" -> "yaml";
            default -> throw new IllegalArgumentException(
                    "Unsupported OpenAPI spec file extension: " + name + " (expected .json/.yaml/.yml)");
        };
    }
}
