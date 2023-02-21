package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선과_구간_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선과_구간_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선과_구간_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When source 와 target 경로 조회 시
     * Then 경로를 확인 할 수 있다
     */
    @DisplayName("경로 조회 시 경로에 있는 역 목록을 확인 할 수 있다")
    @Test
    void 경로_조회_시_경로에_있는_역_목록을_확인_할_수_있다() {
        // When
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 양재역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.TYPE)).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
    }

    /**
     * When 경로 조회 시 출발 역과 도착 역이 같은 경우
     * Then 조회가 안된다
     */
    @DisplayName("경로 조회 시 출발 역과 도착 역이 같은 경우 조회가 안된다")
    @Test
    void 경로_조회_시_출발_역과_도착_역이_같은_경우_조회가_안된다() {
        // When
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 교대역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ExtractableResponse<Response> 경로_조회_요청(Long source, Long target) {
        HashMap<String, Long> parms = new HashMap<>();
        parms.put("source", source);
        parms.put("target", target);

        return RestAssured
            .given().log().all()
            .params(parms)
            .when().get("/paths")
            .then().log().all().extract();
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private Long 지하철_노선과_구간_생성_요청(String name, String color, Long upStationId, Long downStationId, int distance) {
        long lineId = 지하철_노선_생성_요청(name, color).jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(lineId, createSectionCreateParams(upStationId, downStationId, distance));
        return lineId;
    }

}
