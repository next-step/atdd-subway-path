package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.SectionSteps.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static nextstep.subway.acceptance.PathSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관리 기능")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    private Long 개봉역;

    /**
     * 교대역    --- *2호선(10)* --- 강남역
     * |                         |
     * *3호선*(2)                 *신분당선*(10)
     * |                         |
     * 남부터미널역--- *3호선*(3) --- 양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 경로를 조회하면
     * Then 최단 경로 역 목록과 구간의 거리를 찾을 수 있다.
     */
    @Test
    void 최단_거리를_구한다() {
        // when
        ExtractableResponse<Response> 최단_거리_응답 = 지하철_최단_거리_조회_요청(교대역, 양재역);

        // then
        assertAll(
                () -> assertThat(최단_거리_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역,남부터미널역,양재역),
                () -> assertThat(최단_거리_응답.jsonPath().getInt("distance")).isEqualTo(5)
        );
    }

    /**
     * When 출발역과 도약역이 같은 경로를 조회하면
     * Then 실패한다.
     */
    @Test
    void 최단_거리를_구한다_실패1() {
        // when
        ExtractableResponse<Response> 최단_거리_응답 = 지하철_최단_거리_조회_요청(교대역, 교대역);

        // then
        BADREQUEST_실패케이스_검증("출발역과 도착역은 같을 수 없습니다.", 최단_거리_응답, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 그래프에 존재하지 않는 출발역과 도착역을 조회하면
     * Then 실패한다.
     */
    @Test
    void 최단_거리를_구한다_실패2() {
        개봉역 = 지하철역_생성_요청("개봉역").jsonPath().getLong("id");
        // when
        ExtractableResponse<Response> 최단_거리_응답 = 지하철_최단_거리_조회_요청(개봉역, 교대역);

        // then
        BADREQUEST_실패케이스_검증("존재하지 않는 출발역 입니다.", 최단_거리_응답, HttpStatus.BAD_REQUEST);
    }

}
