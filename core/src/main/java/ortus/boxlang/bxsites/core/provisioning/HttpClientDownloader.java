package ortus.boxlang.bxsites.core.provisioning;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

final class HttpClientDownloader implements Downloader {

    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    @Override
    public byte[] fetch(String url) {
        var request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofMinutes(5))
                .GET()
                .build();
        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() / 100 != 2) {
                throw new UncheckedIOException(
                        new IOException("GET " + url + " returned HTTP " + response.statusCode()));
            }
            return response.body();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to download " + url, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UncheckedIOException(new IOException("Interrupted while downloading " + url, e));
        }
    }
}
