package nextstep.subway.acceptance.station;

import nextstep.subway.acceptance.common.CommonAcceptanceTest;
import nextstep.subway.station.presentation.request.CreateStationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.acceptance.station.StationApiExtractableResponse.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends CommonAcceptanceTest {

    private String 강남역 = "강남역";
    private String 광화문역 = "광화문역";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void 지하철_역을_생성() {
        // when & then
        createStation(CreateStationRequest.from(강남역));

        // then
        List<String> stationNames =
                selectStations().jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).contains(강남역);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void 지하철_역_목록을_조회() {
        // given
        createStation(CreateStationRequest.from(강남역));
        createStation(CreateStationRequest.from(광화문역));

        // when
        List<String> stationNames =
                selectStations().jsonPath().getList("stations.name", String.class);

        // then
        assertThat(stationNames).contains(강남역, 광화문역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void 지하철_역을_삭제() {
        // given
        Long stationId = createStation(CreateStationRequest.from(강남역)).jsonPath().getLong("stationId");

        // when
        deleteStation(stationId);

        // then
        List<String> stationNames =
                selectStations().jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).doesNotContain(강남역);
    }

}
