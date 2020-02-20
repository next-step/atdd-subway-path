package atdd;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class HttpTestSupport {

    private final WebTestClient webTestClient;

    public HttpTestSupport(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    protected <T, R> R post(String uri, T request, Class<T> requestType, Class<R> returnType) {
        return webTestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), requestType)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(returnType)
                .returnResult().getResponseBody();
    }

    protected <T> T put(String uri, Class<T> returnType) {
        return webTestClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(returnType)
                .returnResult().getResponseBody();
    }

    protected <T, R> R put(String uri, T request, Class<T> requestType, Class<R> returnType) {
        return webTestClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), requestType)
                .exchange()
                .expectStatus().isOk()
                .expectBody(returnType)
                .returnResult().getResponseBody();
    }

    protected <T> List<T> findAll(String uri, Class<T> returnType) {
        return webTestClient.get()
                .uri(uri)
                .acceptCharset(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(returnType)
                .returnResult().getResponseBody();
    }

    protected <T> T get(String uri, Class<T> returnType) {
        return webTestClient.get()
                .uri(uri)
                .acceptCharset(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(returnType)
                .returnResult().getResponseBody();
    }

}
