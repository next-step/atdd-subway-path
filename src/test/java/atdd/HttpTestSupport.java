package atdd;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

public abstract class HttpTestSupport {

    private final WebTestClient webTestClient;

    public HttpTestSupport(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    protected <T> T create(String uri, Map<String, ?> request, Class<T> type) {
        return create(uri, request, Map.class, type);
    }

    protected <T, R> R create(String uri, T request, Class<T> requestType, Class<R> returnType) {
        return webTestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), requestType)
                .exchange()
                .expectBody(returnType)
                .returnResult().getResponseBody();
    }

}
