package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.LineSteps.신규_라인;
import static nextstep.subway.acceptance.PathSteps.최단_경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.신규_지하철역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선(10)* ---   강남역
     * |                                |
     * *3호선(2)*                   *신분당선(10)*
     * |                                |
     * 남부터미널역  --- *3호선(3)* ---   양재
     */
    @BeforeEach
    public void createData() {
        교대역 = 신규_지하철역("교대역");
        강남역 = 신규_지하철역("강남역");
        양재역 = 신규_지하철역("양재역");
        남부터미널역 = 신규_지하철역("남부터미널역");

        이호선 = 신규_지하철_노선(신규_라인("2호선", "green", 교대역, 강남역, 10L));
        신분당선 = 신규_지하철_노선(신규_라인("신분당선", "red", 강남역, 양재역, 10L));
        삼호선 = 신규_지하철_노선(신규_라인("3호선", "orange", 교대역, 남부터미널역, 2L));

        지하철_노선에_지하철_구간_생성_요청(삼호선, 신규_구간(남부터미널역, 양재역, 3L));
    }

    @DisplayName("출발역과 도착역의 최단거리 조회")
    @Test
    void find_shortest_path() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(강남역, 남부터미널역);

        // then
        assertAll(
                () -> assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "교대역", "남부터미널역"),
                () -> assertThat(response.jsonPath().getLong("distance")).isEqualTo(12)
        );
    }
}
