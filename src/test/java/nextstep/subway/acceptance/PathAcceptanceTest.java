package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import nextstep.subway.acceptance.step.LineStep;
import nextstep.subway.acceptance.step.PathStep;
import nextstep.subway.acceptance.step.SectionStep;
import nextstep.subway.acceptance.step.StationStep;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("경로 관련 기능")
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

        교대역 = StationStep.지하철역을_생성한다("교대역").jsonPath().getLong("id");
        강남역 = StationStep.지하철역을_생성한다("강남역").jsonPath().getLong("id");
        양재역 = StationStep.지하철역을_생성한다("양재역").jsonPath().getLong("id");
        남부터미널역 = StationStep.지하철역을_생성한다("남부터미널역").jsonPath().getLong("id");

        이호선 = LineStep.지하철_노선을_생성한다("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = LineStep.지하철_노선을_생성한다("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = LineStep.지하철_노선을_생성한다("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        SectionStep.지하철_노선_구간을_등록한다(삼호선, 남부터미널역, 양재역, 3);
    }

    /**
     * Given : 지하철역을 4개 생성하고
     * And : 4개의 지하철역을 각각 2개씩 포함하는 3개의 구간을 생성하고
     * And : 하나의 지하철역에 1개의 구간을 추가한 후
     * When : 최단 경로 조회를 요청하면
     * Then : 경로와 거리를 응답한다.
     */
    @DisplayName("출발역으로 부터 도착역으로의 최단경로 조회")
    @Test
    void searchPath() {
        // when
        ExtractableResponse<Response> pathsResponse = PathStep.출발_역에서_도착_역까지의_최단거리_조회(1, 3);

        // then
        assertThat(pathsResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stationNames = pathsResponse.jsonPath().getList("stations.name", String.class);
        int distance = pathsResponse.jsonPath().getInt("distance");

        assertThat(stationNames).hasSize(3);
        assertThat(stationNames).containsExactly("교대역", "남부터미널역", "양재역");
        assertThat(distance).isEqualTo(5);
    }
}
