package atdd.edge;

import atdd.line.LineDto;
import atdd.station.domain.dto.StationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class EdgeAcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    private LineDto secondLine;
    private StationDto gangnamStation;
    private StationDto yeoksamStation;
    private StationDto seonneungStation;

    @BeforeEach
    void setUp() {
        // given
        // 1. "강남역" 지하철역이 등록되어 있다.
        this.gangnamStation = webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just("{\"name\":\"강남역\"}"), String.class)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(StationDto.class)
                .returnResult()
                .getResponseBody();

        // 2. "역삼역" 지하철역이 등록되어 있다.
        this.yeoksamStation = webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just("{\"name\":\"역삼역\"}"), String.class)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(StationDto.class)
                .returnResult()
                .getResponseBody();

        // 3. "선릉역" 지하철역이 등록되어 있다.
        this.seonneungStation = webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just("{\"name\":\"선릉역\"}"), String.class)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(StationDto.class)
                .returnResult()
                .getResponseBody();

        // 4. "2호선" 지하철 노선이 등록되어 있다.
        String input = "{" +
                "\"name\": \"2호선\"," +
                "\"startTime\": \"05:00\"," +
                "\"endTime\": \"23:50\"," +
                "\"intervalTime\": \"10\"" +
                "}";

        this.secondLine = webTestClient.post().uri("/line")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(input), String.class)
                .exchange().expectBody(LineDto.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    public void 지하철노선에_지하철_구간을_등록() {
        // when
        String input = "{" +
                "\"lineId\": " + this.secondLine.getId() + "," +
                "\"sourceStationId\": " + this.gangnamStation.getId() + "," +
                "\"targetStationId\": " + this.yeoksamStation.getId() +
                "}";

        webTestClient.post().uri("/edges")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(input), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(EdgeDto.class)
                .isEqualTo(EdgeDto.builder()
                        .lineId(secondLine.getId())
                        .sourceStationId(gangnamStation.getId())
                        .targetStationId(yeoksamStation.getId())
                        .build());

        // then
        webTestClient.get().uri("/stations/{lineId}", this.secondLine.getId())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(StationDto.class)
                .returnResult().getResponseBody();
    }


    @Test
    public void 지하철노선에_지하철_구간을_제외() {
        // Given
        // "2호선" 지하철 노선에 "강남역"과 "역삼역" 지하철 구간 등록되어 있다.
        String input = "{" +
                "\"lineId\": " + this.secondLine.getId() + "," +
                "\"sourceStationId\": " + this.gangnamStation.getId() + "," +
                "\"targetStationId\": " + this.yeoksamStation.getId() +
                "}";
        webTestClient.post().uri("/edges")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(input), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(EdgeDto.class);

        // And "2호선" 지하철 노선에 "역삼역"과 "선릉역" 지하철 구간 등록되어 있다.
        String input2 = "{" +
                "\"lineId\": " + this.secondLine.getId() + "," +
                "\"sourceStationId\": " + this.yeoksamStation.getId() + "," +
                "\"targetStationId\": " + this.seonneungStation.getId() +
                "}";
        webTestClient.post().uri("/edges")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(input2), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(EdgeDto.class);

        // when
        webTestClient.delete().uri("/edges/{lineId}/{stationId}", this.secondLine.getId(), this.yeoksamStation.getId())
                .exchange()
                .expectStatus().isNoContent();

        // then
        webTestClient.get().uri("/stations/{stationId}", this.yeoksamStation.getId())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(StationDto.class)
                .returnResult().getResponseBody();

        webTestClient.get().uri("/lines/{lineId}", this.secondLine.getId())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(LineDto.class)
                .returnResult().getResponseBody();
    }
}
