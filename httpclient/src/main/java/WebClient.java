import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class WebClient {

    private HttpClient client;

    /* instantiate web client */
    /* Read more about Builder pattern https://en.wikipedia.org/wiki/Builder_pattern*/
    public WebClient() {
        this.client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    }

    /* send task (post http request) asynchronously */
    public CompletableFuture<String> sendTask(String url, byte[] requestPayload) {
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

    /* send task (post http request) asynchronously with custom headers */
    public CompletableFuture<String> sendTask(String url, byte[] requestPayload, String headers) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload));

        if (headers != null && !headers.isBlank()) {
            for (String header : headers.split("\\R")) {
                if (header.isBlank()) {
                    continue;
                }
                int colonIndex = header.indexOf(':');
                if (colonIndex <= 0) {
                    throw new IllegalArgumentException("Invalid HTTP header: " + header);
                }
                String name = header.substring(0, colonIndex).trim();
                String value = header.substring(colonIndex + 1).trim();
                requestBuilder.header(name, value);
            }
        }

        return client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
