package atdd.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SubwayAcceptanceTestSupport {
    @Autowired
    protected WebTestClient webTestClient;

    protected <T> EntityExchangeResult<T> getResource(String locationPath,
                                                      Class<T> responseBody) {

        return webTestClient.get()
                .uri(locationPath)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(responseBody)
                .returnResult();
    }

    protected String createResource(String requestPath,
                                    Object requestDto) {

        EntityExchangeResult<Void> result = webTestClient.post()
                .uri(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), Object.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Void.class)
                .returnResult();

        return result.getResponseHeaders().getLocation().getPath();
    }

    protected Long extractId(String locationPath) {
        return Long.parseLong(locationPath.split("/")[2]);
    }
}
