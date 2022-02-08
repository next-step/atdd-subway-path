package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

class PathAcceptanceTest extends AcceptanceTest{

    private static int DEFAULT_DISTANCE;

    public static Long 일호선;
    public static Long 이호선;
    public static Long 삼호선;
    public static Long 신분당선;

    public static Long 시청역;
    public static Long 서울역;
    public static Long 용산역;
    public static Long 교대역;
    public static Long 강남역;
    public static Long 역삼역;
    public static Long 고속터미널역;
    public static Long 남부터미널역;
    public static Long 양재역;
    public static Long 판교역;


    /**
     *
     * 시청--서울--용산 (1호선)
     *
     * 고속터미널 (3호선)
     *  |
     * 교대--------강남---------역삼 (2호선)
     *  |          |
     * 남부터미널----양재
     *             |
     *            판교 (신분당선)
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        시청역 = 지하철역_생성_요청("시청역").jsonPath().getLong("id");
        서울역 = 지하철역_생성_요청("서울역").jsonPath().getLong("id");
        용산역 = 지하철역_생성_요청("용산역").jsonPath().getLong("id");
        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        고속터미널역 = 지하철역_생성_요청("고속터미널역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        일호선 = 지하철_노선_생성_요청("일호선", "blue").jsonPath().getLong("id");
        이호선 = 지하철_노선_생성_요청("이호선", "green").jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("삼호선", "orange").jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(일호선, createSectionParams(시청역, 서울역, 10));
        지하철_노선에_지하철_구간_생성_요청(일호선, createSectionParams(서울역, 용산역, 10));
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionParams(교대역, 강남역, 10));
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionParams(강남역, 역삼역, 10));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionParams(고속터미널역, 교대역, 10));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionParams(강남역, 남부터미널역, 10));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(강남역, 양재역, 10));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 판교역, 10));
    }

    @DisplayName("최단 경로 조회")
    @Test
    void paths() {
        //when
        ExtractableResponse<Response> 최단_경로_응답 = 최단_경로_조회(교대역, 판교역);

        //then
        최단_경로_역_목록_확인(최단_경로_응답, 교대역, 강남역, 양재역, 판교역);
    }

    @DisplayName("도착역이 없는 경우 최단 경로 조회 실패")
    @Test
    void pathsNotExistArrivalStation() {
        //when
        ExtractableResponse<Response> 최단_경로_응답 = 최단_경로_조회(교대역, 999L);

        //then
        최단_경로_조회_실패(최단_경로_응답);
    }

    @DisplayName("출발역이 없는 경우 최단 경로 조회 실패")
    @Test
    void pathsNotExistStartStation() {
        //when
        ExtractableResponse<Response> 최단_경로_응답 = 최단_경로_조회(999L, 교대역);

        //then
        최단_경로_조회_실패(최단_경로_응답);
    }

    @DisplayName("출발역과 도착역이 동일한 경우 최단 경로 조회 실패")
    @Test
    void pathsSameStations() {
        //when
        ExtractableResponse<Response> 최단_경로_응답 = 최단_경로_조회(교대역, 교대역);

        //then
        최단_경로_조회_실패(최단_경로_응답);
    }

    @DisplayName("출발역에서  도착역을 도달할 수 없는 경우 최단 경로 조회 실패")
    @Test
    void pathsCannotReachArrivalStation() {
        //when
        ExtractableResponse<Response> 최단_경로_응답 = 최단_경로_조회(강남역, 시청역);

        //then
        최단_경로_조회_실패(최단_경로_응답);
    }

    private Map<String, String> createSectionParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
