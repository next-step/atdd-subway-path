package atdd.station;

import atdd.station.domain.SubwayLine;
import atdd.station.web.dto.SubwayLineResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubwayLineAcceptanceTest {
    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("지하철역 노선을 등록한다")
    @Test
    public void create() {
        // expect
        createSubwayLine("2호선");
    }

    private EntityExchangeResult<Void> createSubwayLine(String subwayLineName) {
        // given
        String inputJson = "{\"name\":\"" + subwayLineName + "\"}";

        return webTestClient.post()
                .uri("/subway-lines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Void.class)
                .returnResult();
    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    public void retrieveSubwayLines() {
        // given
        createSubwayLine("2호선");
        createSubwayLine("8호선");

        // when, then
        webTestClient.get()
                .uri("/subway-lines")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(SubwayLineResponseDto.class)
                .hasSize(2)
                .isEqualTo(Arrays.asList(SubwayLineResponseDto.of(SubwayLine.of("2호선")), SubwayLineResponseDto.of(SubwayLine.of("8호선"))));
    }

    @DisplayName("지하철역 노선 정보를 조회한다")
    @ParameterizedTest
    @ValueSource(strings = {"2호선", "8호선", "5호선"})
    public void retrieveFromSubwayLineName(String subwayLineName) {
        // given
        EntityExchangeResult<Void> createdResult = createSubwayLine(subwayLineName);

        // when, then
        getFromSubwayLineName(subwayLineName, createdResult);
    }

    private void getFromSubwayLineName(String subwayLineName,
                                       EntityExchangeResult<Void> createdResult) {

        webTestClient.get()
                .uri(createdResult.getResponseHeaders().getLocation().getPath())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(SubwayLineResponseDto.class)
                .isEqualTo(SubwayLineResponseDto.of(SubwayLine.of(subwayLineName)));
    }

}
