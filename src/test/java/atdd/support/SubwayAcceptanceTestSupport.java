package atdd.support;

import atdd.station.application.dto.StationResponseDto;
import atdd.station.application.dto.SubwayLineResponseDto;
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

    protected EntityExchangeResult<Void> createStation(String stationName) {
        String inputJson = "{\"name\":\"" + stationName + "\"}";

        return webTestClient.post()
                .uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Void.class)
                .returnResult();
    }

    protected EntityExchangeResult<StationResponseDto> getStationFromLocationPath(String locationPath) {
        return webTestClient.get()
                .uri(locationPath)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StationResponseDto.class)
                .returnResult();
    }

    protected EntityExchangeResult<StationResponseDto> getStationFromId(Long stationId) {

        return webTestClient.get()
                .uri("/stations/" + stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StationResponseDto.class)
                .returnResult();
    }

    protected EntityExchangeResult<Void> createSubwayLine(String subwayLineName) {
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

    protected EntityExchangeResult<SubwayLineResponseDto> getSubwayLineFromLocationPath(String locationPath) {

        return webTestClient.get()
                .uri(locationPath)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(SubwayLineResponseDto.class)
                .returnResult();
    }

    protected EntityExchangeResult<SubwayLineResponseDto> getSubwayLineFromId(Long subwayLineId) {

        return webTestClient.get()
                .uri("/subway-lines/" + subwayLineId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(SubwayLineResponseDto.class)
                .returnResult();
    }

    protected EntityExchangeResult<Void> createSubwaySection(Long subwayLineId, Long sourceStationId, Long targetStationId) {
        String inputJson = "{\"sourceStationId\":" + sourceStationId + ", \"targetStationId\":" + targetStationId + "}";

        return webTestClient.post()
                .uri("/subway-lines/" + subwayLineId + "/subway-section")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Void.class)
                .returnResult();
    }

    protected Long extractId(EntityExchangeResult<Void> result) {
        return Long.parseLong(result.getResponseHeaders().getLocation().getPath().split("/")[2]);
    }
}
