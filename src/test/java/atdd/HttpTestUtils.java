package atdd;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class HttpTestUtils {
    private WebTestClient webTestClient;

    public HttpTestUtils(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public <T> EntityExchangeResult postRequest(final String uri, String inputJson, Class<T> bodyType) {
        return webTestClient.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(bodyType).returnResult();
    }

    public <T> EntityExchangeResult getRequest(final String uri, ParameterizedTypeReference<T> bodyType) {
        return webTestClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(bodyType).returnResult();
    }

    public void deleteRequest(final String uri) {
        EntityExchangeResult result = webTestClient.delete().uri(uri)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().returnResult();
    }
}
