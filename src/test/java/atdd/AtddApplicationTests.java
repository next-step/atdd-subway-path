package atdd;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AtddApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(AtddApplicationTests.class);

    @Autowired
    private WebTestClient client;

    @Test
    void contextLoads() {

        String name = "강남역";
        String json = "{\"name\":\""+name+"\")";

        client.post()
                .uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(json), String.class)
                .exchange()
                .expectStatus().isCreated();
    }

}
