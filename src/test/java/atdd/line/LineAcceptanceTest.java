package atdd.line;

import atdd.line.api.response.LineResponseView;
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
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static atdd.util.TestUtils.getLocationPath;
import static org.assertj.core.api.Assertions.assertThat;

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

        EntityExchangeResult<LineResponseView> result = createLine(lineName);
        LineResponseView line = result.getResponseBody();

        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo(lineName);
    }

    @DisplayName("지하철역 노선 목록 조회를 할 수 있다")
    @Test
    void beAbleFindLines() throws Exception {
        String lineName = "2호선";
        createLine(lineName);

        webTestClient.get().uri("/lines")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(1)
                .jsonPath("$.lines[0].name", lineName);
    }

    @DisplayName("지하철역 노선 정보를 조회 할 수 있다")
    @Test
    void beAbleFindLineById() throws Exception {
        String lineName = "2호선";

        EntityExchangeResult<LineResponseView> result = createLine(lineName);
        String path = getLocationPath(result.getResponseHeaders());

        webTestClient.get().uri(path)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.lines[0].name", lineName);
    }

    private EntityExchangeResult<LineResponseView> createLine(String lineName) throws Exception {
        String inputJson = getInputJson(lineName);

        return webTestClient.post().uri("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(LineResponseView.class)
                .returnResult();
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
