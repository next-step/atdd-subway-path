package atdd.station;

import atdd.station.application.dto.StationResponseDto;
import atdd.station.domain.Station;
import atdd.support.SubwayAcceptanceTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends SubwayAcceptanceTestSupport {

    @DisplayName("지하철역을 등록한다")
    @ParameterizedTest
    @ValueSource(strings = {"강남역", "잠실역", "장한평역"})
    public void create(String stationName) {
        // expect
        createStation(stationName);
    }

    @DisplayName("지하철역 목록을 조회한다")
    @Test
    public void retrieveStations() {
        // given
        createStation("강남역");
        createStation("잠실역");

        // when, then
        webTestClient.get()
                .uri("/stations")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(StationResponseDto.class)
                .hasSize(2)
                .isEqualTo(Arrays.asList(StationResponseDto.of(Station.of("강남역"), Collections.emptyList()), StationResponseDto.of(Station.of("잠실역"), Collections.emptyList())));
    }

    @DisplayName("지하철역을 조회한다")
    @Test
    public void retrieveStation() {
        // given
        String stationName = "강남역";
        EntityExchangeResult<Void> createdResult = createStation(stationName);

        String locationPath = createdResult.getResponseHeaders().getLocation().getPath();

        // when, then
        assertThat(getStationFromLocationPath(locationPath).getResponseBody().getName())
                .isEqualTo(stationName);
    }

    @DisplayName("지하철역 삭제한다")
    @Test
    public void deleteFromStationName() {
        // given
        String stationName = "강남역";
        EntityExchangeResult<Void> createdResult = createStation(stationName);

        // when
        webTestClient.delete()
                .uri(createdResult.getResponseHeaders().getLocation().getPath())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        // then
        webTestClient.get()
                .uri(createdResult.getResponseHeaders().getLocation().getPath())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

}
