package ortus.boxlang.bxsites.core.provisioning;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * A minimal HTTP-GET-to-string/bytes abstraction, kept separate from
 * {@link Provisioner} purely so tests can substitute a fake instead of
 * hitting the real network.
 */
public interface Downloader {

    /**
     * Fetches the raw bytes at {@code url}.
     *
     * @throws UncheckedIOException if the request fails or does not return
     *                              a successful status
     */
    byte[] fetch(String url);

    /** Default implementation, backed by {@link java.net.http.HttpClient}. */
    static Downloader httpClient() {
        return new HttpClientDownloader();
    }
}
