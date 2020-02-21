package atdd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTestSupport {

    @Autowired
    protected WebTestClient webTestClient;

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
