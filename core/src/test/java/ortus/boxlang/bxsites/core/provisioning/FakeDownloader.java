package ortus.boxlang.bxsites.core.provisioning;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/** An in-memory {@link Downloader} for tests - no real network involved. */
final class FakeDownloader implements Downloader {

    private final Map<String, byte[]> content = new HashMap<>();
    private int fetchCount = 0;

    /** Registers {@code bytes} at {@code url}, and its matching sha-256 checksum file at {@code url + ".sha-256"}. */
    void put(String url, byte[] bytes) {
        content.put(url, bytes);
        content.put(url + ".sha-256", (digestHex(bytes, "SHA-256") + "  somefile\n").getBytes());
    }

    /** Registers {@code bytes} at {@code url}, and its matching sha-512 checksum file at {@code url + ".sha512"}. */
    void putWithSha512(String url, byte[] bytes) {
        content.put(url, bytes);
        content.put(url + ".sha512", (digestHex(bytes, "SHA-512") + "  somefile\n").getBytes());
    }

    int fetchCount() {
        return fetchCount;
    }

    @Override
    public byte[] fetch(String url) {
        fetchCount++;
        byte[] bytes = content.get(url);
        if (bytes == null) {
            throw new IllegalStateException("No fake content registered for " + url);
        }
        return bytes;
    }

    private static String digestHex(byte[] data, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
