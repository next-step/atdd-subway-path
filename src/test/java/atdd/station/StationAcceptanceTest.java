package atdd.station;

import atdd.BaseAcceptanceTest;
import atdd.station.api.response.StationResponseView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import static atdd.TestConstant.STATION_NAME;
import static atdd.TestConstant.TEST_STATION;
import static atdd.station.controller.StationController.STATION_URL;
import static atdd.util.TestUtils.getLocationPath;
import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends BaseAcceptanceTest {

    @DisplayName("지하철역 등록을 할 수 있다")
    @Test
    void beAbleCreateStation() {
        EntityExchangeResult<StationResponseView> result = createStation(TEST_STATION);
        StationResponseView station = result.getResponseBody();

        assertThat(station).isNotNull();
        assertThat(station.getName()).isEqualTo(STATION_NAME);
    }

    @DisplayName("지하철역 목록을 조회 할 수 있다")
    @Test
    void beAbleFindStations() {
        createStation(TEST_STATION);

        webTestClient.get().uri(STATION_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.count").isEqualTo(1)
                .jsonPath("$.stations[0].name").isEqualTo(STATION_NAME);
    }

    @DisplayName("지하철역 정보를 조회 할 수 있다")
    @Test
    void beAbleFindStationsById() {
        EntityExchangeResult<StationResponseView> result = createStation(TEST_STATION);
        String path = getLocationPath(result.getResponseHeaders());

        webTestClient.get().uri(path)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(STATION_NAME);
    }

    @DisplayName("지하철역 정보를 삭제 할 수 있다")
    @Test
    void beAbleDeleteStation() {
        EntityExchangeResult<StationResponseView> result = createStation(TEST_STATION);
        String path = getLocationPath(result.getResponseHeaders());

        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNoContent();
    }

}
