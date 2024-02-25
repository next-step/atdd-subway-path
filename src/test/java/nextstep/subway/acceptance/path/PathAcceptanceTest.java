package nextstep.subway.acceptance.path;

import static nextstep.subway.acceptance.AcceptanceTestBase.assertStatusCode;
import static nextstep.subway.acceptance.ResponseParser.getIdFromResponse;
import static nextstep.subway.acceptance.ResponseParser.getStationIdsFromResponse;
import static nextstep.subway.acceptance.ResponseParser.getStringIdFromResponse;
import static nextstep.subway.acceptance.line.LineAcceptanceTestHelper.노선_생성_요청;
import static nextstep.subway.acceptance.line.LineAcceptanceTestHelper.노선_파라미터_생성;
import static nextstep.subway.acceptance.path.PathAcceptanceTestHelper.노선_조회_요청;
import static nextstep.subway.acceptance.section.SectionAcceptanceTestHelper.구간_등록_요청;
import static nextstep.subway.acceptance.section.SectionAcceptanceTestHelper.구간_파라미터_생성;
import static nextstep.subway.acceptance.station.StationAcceptanceTestHelper.지하철_파라미터_생성;
import static nextstep.subway.acceptance.station.StationAcceptanceTestHelper.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@AcceptanceTest
public class PathAcceptanceTest {
    private String 교대역Id;
    private String 남부터미널역Id;
    private String 양재역Id;
    private String 강남역Id;
    private String 반포역Id;
    private String 학동역Id;

    private static String 교대역_남부터미널역_거리 = 3;
    private static String 남부터미널역_양재역_거리 = 4;
    private static String 교대역_강남역_거리 = 1;
    private static String 양재역_강남역_거리 = 2;

    private static String 반포역_학동역_거리 = 5;

    @BeforeEach
    void setStation() {
        교대역Id = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성("교대역")));
        남부터미널역Id = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성("남부터미널역")));
        양재역Id = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성("양재역")));
        강남역Id = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성("강남역")));
        반포역Id = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성("반포역")));
        학동역Id = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성("학동역")));
    }


    /**
     * 교대역    --- *2호선* (1) ---   강남역           반포역  --- *7호선* (5) ---  학동역
     * |                              |
     * *3호선*(3)                    *신분당선*(2)
     * |                              |
     * 남부터미널역  --- *3호선*(4) ---  양재역
     *
     */
    void setLine() {
        var lineResponse = 노선_생성_요청(노선_파라미터_생성("3호선", 교대역Id, 남부터미널역Id, 교대역_남부터미널역_거리));
        구간_등록_요청(구간_파라미터_생성(남부터미널역Id, 양재역Id), getIdFromResponse(lineResponse));
        노선_생성_요청(노선_파라미터_생성("2호선", 교대역Id, 강남역Id, 교대역_강남역_거리));
        노선_생성_요청(노선_파라미터_생성("신분당선", 강남역Id, 양재역Id, 양재역_강남역_거리));
        노선_생성_요청(노선_파라미터_생성("7호선", 반포역Id, 학동역Id, 반포역_학동역_거리));

    }


    /**
     * given 여러개의 노선에 환승역이 존재하는 구간을 등록하고
     * when 두 역 간의 경로를 조회하면
     * then 두 역 사이의 최단 경로에 있는 역들을 조회할 수 있다.
     */
    @Test
    @DisplayName("출발역과 도착역을 입력하면 최단거리와 경유지를 모두 조회할 수 있다.")
    void PathAcceptanceTest() {
        // given
        setLine();

        // when
        var response = 노선_조회_요청(교대역Id, 양재역Id);
        List<String> stationIds = getStationIdsFromResponse(response);
        Integer distance = response.jsonPath().getInt("distance");

        // then
        assertStatusCode(response, HttpStatus.OK);
        assertThat(stationIds).containsExactly(교대역Id, 강남역Id, 양재역Id);
        assertThat(distance).isEqualTo(3.0);
    }
}
