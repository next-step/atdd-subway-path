package atdd.station.controller;

import atdd.station.dto.LineDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import reactor.core.publisher.Mono;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class LineAcceptanceTest
{
    private final String BASE_URL = "/lines/";
    private final String STATIOIN_NAME = "강남역";
    private final String LINE_NAME = "2호선";
    private static final String INPUT_JSON = "{" +
            "\"name\":\""+ "2호선" +"\"," +
            "\"startTime\":\""+ "05:00" +"\"," +
            "\"endTime\":\""+ "23:50" +"\"," +
            "\"intervalTime\":\""+ "10" +"\"" +
            "}";


    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("지하철 노선 등록")
    public void createLine()
    {
        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(INPUT_JSON), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo("2호선")
                .jsonPath("$.startTime").isEqualTo("05:00")
                .jsonPath("$.endTime").isEqualTo("23:50")
                .jsonPath("$.intervalTime").isEqualTo("10");
    }

    @Test
    @DisplayName("지하철 노선 목록 조회")
    public void findLines()
    {
        createLine(LINE_NAME);

        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.[0].name").isEqualTo(LINE_NAME);
    }

    @Test
    @DisplayName("지하철 노선 정보 조회")
    public void detailById()
    {
        createLine(LINE_NAME);

        webTestClient.get().uri(BASE_URL + 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.name").isEqualTo(LINE_NAME);
    }

    @Test
    @DeleteMapping("지하철 노선 삭제")
    public void deleteLine()
    {
        createLine();

        webTestClient.delete().uri(BASE_URL + 1)
                .exchange()
                .expectStatus().isOk();
    }

    public void createLine(String lineName)
    {
        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(INPUT_JSON), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo("2호선")
                .jsonPath("$.startTime").isEqualTo("05:00")
                .jsonPath("$.endTime").isEqualTo("23:50")
                .jsonPath("$.intervalTime").isEqualTo("10");
    }
}
