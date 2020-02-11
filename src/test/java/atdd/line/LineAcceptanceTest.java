package atdd.line;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class LineAcceptanceTest {

    private static final Logger logger = LoggerFactory.getLogger(LineAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("지하철역 노선 등록을 할 수 있다")
    @Test
    void beAbleCreateLine() throws Exception {
        String lineName = "2호선";
        String inputJson = getInputJson(lineName);

        webTestClient.post().uri("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(lineName);
    }

    private String getInputJson(String lineName) throws JsonProcessingException {
        final Map<String, Object> inputMap = Map.ofEntries(
                Map.entry("name", lineName),
                Map.entry("startTime", "05:30"),
                Map.entry("endTime", "00:30"),
                Map.entry("intervalTime", 5)
        );

        return objectMapper.writeValueAsString(inputMap);
    }

}
