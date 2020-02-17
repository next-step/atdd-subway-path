package atdd.support;

import atdd.station.application.dto.StationResponseDto;
import atdd.station.application.dto.SubwayLineResponseDto;
import atdd.station.web.dto.SubwaySectionCreateRequestDto;
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

    protected EntityExchangeResult<SubwayLineResponseDto> getSubwayLineResource(Long subwayLineId) {
        return getResource("/subway-lines/" + subwayLineId, SubwayLineResponseDto.class);
    }

    protected EntityExchangeResult<StationResponseDto> getStationResource(Long stationId) {
        return getResource("/stations/" + stationId, StationResponseDto.class);
    }

    protected String createResource(String requestPath,
                                    Object requestDto) {

        EntityExchangeResult<Void> result = webTestClient.post()
                .uri(requestPath)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), Object.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Void.class)
                .returnResult();

        return result.getResponseHeaders().getLocation().getPath();
    }

    protected Long createStationResource(String stationName) {
        String locationPath = createResource("/stations", stationName);
        return extractId(locationPath);
    }

    protected Long createSubwayLineResource(String subwayLineName) {
        String locationPath = createResource("/subway-lines", subwayLineName);
        return extractId(locationPath);
    }

    protected Long createSubwaySectionResource(Long createdSubwayLineId,
                                               Long sourceStationId,
                                               Long targetStationId) {
        String locationPath = createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(sourceStationId, targetStationId));
        return extractId(locationPath);
    }

    protected Long extractId(String locationPath) {
        return Long.parseLong(locationPath.split("/")[2]);
    }

    protected <T> EntityExchangeResult<T> changeResource(String requestPath,
                                                         Object requestDto,
                                                         Class<T> responseBody) {

        return webTestClient.put()
                .uri(requestPath)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), Object.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(responseBody)
                .returnResult();
    }

    protected void deleteResource(String requestPath) {

        webTestClient.delete()
                .uri(requestPath)
                .exchange()
                .expectStatus().isOk();
    }

    protected void deleteStationResource(Long stationId) {
        deleteResource("/stations/" + stationId);
    }

    protected void deleteSubwayLineResource(Long subwayLineId) {
        deleteResource("/subway-lines/" + subwayLineId);
    }

    protected void deleteSubwaySectionResource(Long subwayLineId,
                                               String stationName) {
        deleteResource("/subway-lines/" + subwayLineId + "/subway-section?" + "stationName=" + stationName);
    }

}
