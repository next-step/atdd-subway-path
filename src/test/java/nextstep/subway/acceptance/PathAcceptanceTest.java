package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.PathSteps.최단_경로_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 없는역 = 1234L;

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

        이호선 = 지하철_노선_생성_요청("2호선", "green").jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange").jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(교대역, 강남역, 10));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 3));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(교대역, 남부터미널역, 5));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 5));
    }

    @DisplayName("출발역이 존재하지 않는경우 예외발생")
    @Test
    public void not_exists_start_station() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회(없는역, 강남역);

        // then
        생성_실패_확인(response, HttpStatus.NOT_FOUND, "해당 역을 찾을 수 없습니다");
    }

    @DisplayName("도착역이 존재하지 않는경우 예외발생")
    @Test
    public void not_exists_arrival_station() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회(강남역, 없는역);

        // then
        생성_실패_확인(response, HttpStatus.NOT_FOUND, "해당 역을 찾을 수 없습니다");
    }

    @DisplayName("출발역과 도착역의 최단거리를 정상 조회한다")
    @Test
    public void find_shortest_path() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회(강남역, 남부터미널역);

        // then
        assertAll(
                () -> assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "양재역", "남부터미널역"),
                () -> assertThat(response.jsonPath().getLong("distance")).isEqualTo(8)
        );
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance.toString());
        return params;
    }
}
