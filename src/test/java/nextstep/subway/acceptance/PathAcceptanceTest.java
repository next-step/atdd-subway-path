package nextstep.subway.acceptance;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.application.dto.PathResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.acceptance.PathSteps.경로_조회를_요청한다;
import static nextstep.subway.acceptance.SectionSteps.구간을_등록한다;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
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
    void init() {
        교대역 = 지하철역_생성_요청("교대역");
        강남역 = 지하철역_생성_요청("강남역");
        양재역 = 지하철역_생성_요청("양재역");
        남부터미널역= 지하철역_생성_요청("남부터미널역");

        이호선 = 노선이_생성되어_있다("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 노선이_생성되어_있다("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 노선이_생성되어_있다("3호선", "orange", 교대역, 남부터미널역, 2);

        구간을_등록한다(삼호선, 남부터미널역, 양재역, 3);
    }
    /**
     *  Given 지하철역과 2호선 3호선이 등록되어 있다.
     *  When 교대역과 양재역 사이의 경로 조회를 요청한다.
     *  Then 가중치가 적은 3호선의 지하철역을 리턴한다.
     *  And 3호선의 가중치 5를 리턴한다.
     */
    @DisplayName("최단거리 경로를 조회한다.")
    @Test
    public void pathFind() {
        // When
        final ExtractableResponse<Response> response = 경로_조회를_요청한다(교대역, 양재역);

        // Then
        최단거리_지하철역을_리턴한다(response, Arrays.asList( 교대역.intValue(), 남부터미널역.intValue(), 양재역.intValue()));
        최단거리_가중치를_리턴한다(response, 5);
    }

    private void 최단거리_지하철역을_리턴한다(final ExtractableResponse<Response> response, final List<Integer> stationList) {
        final List<Integer> ids = response.jsonPath().getList("stations.id");
        assertThat(stationList).containsExactlyElementsOf(ids);
    }

    private void 최단거리_가중치를_리턴한다(final ExtractableResponse<Response> response, final double expectedDistance) {
        final double actualDistance = response.as(PathResponse.class).getDistance();
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    private static Long 지하철역_생성_요청(final String name) {
        return StationSteps.지하철역_생성_요청(name).jsonPath().getLong("id");
    }

    private static Long 노선이_생성되어_있다(final String name, final String color, final Long upStationId, final Long downStationId,
                                   final int distance) {
        return LineSteps.노선이_생성되어_있다(name, color, upStationId, downStationId, distance).as(LineResponse.class).getId();
    }
}
