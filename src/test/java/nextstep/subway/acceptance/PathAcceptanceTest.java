package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.최단거리_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.error.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

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

        이호선 = 지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 출발역과 도착역에 대한 최단 경로를 요청하면
     * Then 최단 경로의 역들과 거리가 응답된다.
     */
    @DisplayName("출발역과 도착역 사이의 최단 거리 요청")
    @Test
    void findPath() {
        // when
        final ExtractableResponse<Response> 최단거리_응답 = 최단거리_요청(교대역, 양재역);

        // then
        요청이_정상적으로_처리되었는지_확인(최단거리_응답, OK);
        final List<Long> 최단거리_경로_역들 = 최단거리_응답.jsonPath()
                .getList("stations", Map.class).stream()
                .map(it -> Long.valueOf(String.valueOf(it.get("id"))))
                .collect(Collectors.toList());
        assertThat(최단거리_경로_역들).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(최단거리_응답.jsonPath().getInt("distance")).isEqualTo(5);
    }

    /**
     * When 출발역과 도착역이 같도록 하여 최단 경로를 요청하면
     * Then 출발역과 도착역이 동일하다는 예외가 발생한다.
     */
    @DisplayName("[Error] 출발역과 도착역이 같은 경우, 역의 최단 거리 요청 실패")
    @Test
    void findPathWithSameSourceAndTarget() {
        // when
        final ExtractableResponse<Response> 최단거리_응답 = 최단거리_요청(교대역, 교대역);

        // then
        요청이_정상적으로_처리되었는지_확인(최단거리_응답, BAD_REQUEST);
        assertThat(최단거리_응답.jsonPath().getString("message")).isEqualTo(SOURCE_AND_TARGET_IS_SAME.getMessage());
    }

    /**
     * Given 출발역과 연결되어있지 않은 노선을 생성하고
     * When 연결되어있지 않은 출발역과 도착역에 대한 최단 경로를 요청하면
     * Then 출발역과 도착역이 연결되어있지 않다는 에러가 발생한다
     */
    @DisplayName("[Error] 출발역과 도착역이 연결되어있지 않은 경우, 역의 최단 거리 요청 실패")
    @Test
    void findPathWithDisConnectSourceAndTargetStation() {
        // given
        final long 언주역 = 지하철역_생성_요청("언주역").jsonPath().getLong("id");
        final long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선_생성_요청(createLineCreateParams("9호선", "gold", 언주역, 신논현역, 10)).jsonPath().getLong("id");

        // when
        final ExtractableResponse<Response> 최단거리_응답 = 최단거리_요청(교대역, 신논현역);

        // then
        요청이_정상적으로_처리되었는지_확인(최단거리_응답, BAD_REQUEST);
        assertThat(최단거리_응답.jsonPath().getString("message")).isEqualTo(SOURCE_AND_TARGET_IS_DISCONNECTED.getMessage());
    }

    /**
     * Given
     */
    @DisplayName("[Error] 존재하지 않는 출발역이나 도착역을 통해 역의 최단 거리 요청 시, 실패")
    @Test
    void findPathWithNonExistsStation() {
        // given
        final long 언주역 = 지하철역_생성_요청("언주역").jsonPath().getLong("id");

        // when
        final ExtractableResponse<Response> 최단거리_응답 = 최단거리_요청(교대역, 언주역);

        // then
        요청이_정상적으로_처리되었는지_확인(최단거리_응답, BAD_REQUEST);
        assertThat(최단거리_응답.jsonPath().getString("message")).isEqualTo(STATION_NOT_FOUND_IN_SECTION.getMessage());
    }

    private Map<String, Object> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
