package nextstep.subway.acceptance.station;

import nextstep.subway.acceptance.util.CommonAcceptanceTest;
import nextstep.subway.common.Constant;
import nextstep.subway.station.presentation.request.CreateStationRequest;
import nextstep.subway.station.presentation.response.CreateStationResponse;
import nextstep.subway.station.presentation.response.ShowAllStationsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.station.StationApiExtractableResponse.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends CommonAcceptanceTest {

    private CreateStationRequest 강남역_생성_요청;
    private CreateStationRequest 신논현역_생성_요청;

    @BeforeEach
    protected void setUp() {
        강남역_생성_요청 = CreateStationRequest.from(Constant.강남역);
        신논현역_생성_요청 = CreateStationRequest.from(Constant.신논현역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void 지하철_역을_생성() {
        // given
        CreateStationResponse 강남역_생성_응답 = 지하철_역_생성(강남역_생성_요청).as(CreateStationResponse.class);

        // when
        ShowAllStationsResponse 지하철_역_목록_조회_응답 = 지하철_역_목록_조회().as(ShowAllStationsResponse.class);

        // then
        지하철_역_생성_검증(강남역_생성_응답, 지하철_역_목록_조회_응답);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 생성한 2개의 역을 찾을 수 있다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void 지하철_역_목록을_조회() {
        // given
        CreateStationResponse 강남역_생성_응답 = 지하철_역_생성(강남역_생성_요청).as(CreateStationResponse.class);
        CreateStationResponse 광화문_생성_응답 = 지하철_역_생성(신논현역_생성_요청).as(CreateStationResponse.class);

        // when
        ShowAllStationsResponse 지하철_역_목록_조회_응답 = 지하철_역_목록_조회().as(ShowAllStationsResponse.class);

        // then
        지하철_역_생성_검증(강남역_생성_응답, 지하철_역_목록_조회_응답);
        지하철_역_생성_검증(광화문_생성_응답, 지하철_역_목록_조회_응답);
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
        Long 강남역_ID = 지하철_역_생성(강남역_생성_요청).as(CreateStationResponse.class).getStationId();

        // when
        노선_삭제(강남역_ID);

        // then
        ShowAllStationsResponse 지하철_역_목록_조회_응답 = 지하철_역_목록_조회().as(ShowAllStationsResponse.class);
        지하철_역_삭제_검증(강남역_ID, 지하철_역_목록_조회_응답);
    }

    void 지하철_역_생성_검증(CreateStationResponse createStationResponse, ShowAllStationsResponse stationsResponse) {
        assertTrue(stationsResponse.getStations().stream()
                .anyMatch(station -> station.getStationId().equals(createStationResponse.getStationId())));
        assertTrue(stationsResponse.getStations().stream()
                .anyMatch(station -> station.getName().equals(createStationResponse.getName())));
    }

    void 지하철_역_삭제_검증(Long stationId, ShowAllStationsResponse stationsResponse) {
        assertTrue(stationsResponse.getStations().stream()
                .noneMatch(station -> station.getStationId().equals(stationId)));
    }

}
