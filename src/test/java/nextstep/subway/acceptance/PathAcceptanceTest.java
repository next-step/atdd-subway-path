package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.최단거리를_조회할_수_없다;
import static nextstep.subway.acceptance.PathSteps.출발역_도착역_사이_경로_및_최단거리_조회;
import static nextstep.subway.acceptance.SectionSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 탐색")
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

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 출발역과 도착역을 입력하면
     * Then 출발역과 도착역 사이 경로와 최단거리를 알 수 있다.
     */
    @DisplayName("출발역과 도착역 사이 경로와 최단거리 조회")
    @Test
    void showPathsAndDistance() {
        //When
        ExtractableResponse<Response> response = 출발역_도착역_사이_경로_및_최단거리_조회(교대역, 양재역);

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
    }

    /**
     * When 출발역과 도착역을 같은 역을 입력하면
     * Then 같은 역은 조회할 수 없다.
     */

    @DisplayName("출발역과 도착역이 같으면 조회할 수 없다")
    @Test
    void canNotSearchWhenSourceTargetIsSame() {
        //When
        ExtractableResponse<Response> response = 출발역_도착역_사이_경로_및_최단거리_조회(교대역, 교대역);

        //Then
        최단거리를_조회할_수_없다(response);
    }

    /**
     * Given 연결되어 있지 않는 지하철 노선과 구간, 역을 생성하고
     * When 서로 연결되어 있지 않은 출발역과 도착역을 입력하면
     * Then 연결되어 있지 않아서 조회할 수 없다.
     */

    @DisplayName("출발역과 도착역이 연결되어 있지 않으면 조회할 수 없다")
    @Test
    void canNotSearchWhenSourceTargetNotLinked() {
        //Given
        Long 마곡역 = 지하철역_생성_요청("마곡역").jsonPath().getLong("id");
        Long 발산역 = 지하철역_생성_요청("발산역").jsonPath().getLong("id");
        Long 오호선 = 지하철_노선_생성_요청("5호선", "orange", 마곡역, 발산역, 2);

        //When
        ExtractableResponse<Response> response = 출발역_도착역_사이_경로_및_최단거리_조회(교대역, 마곡역);

        //Then
        최단거리를_조회할_수_없다(response);
    }

    /**
     * Given 노선에 없는 역을 생성하고
     * When 노선에 없는 역을 포함하여 입력하면
     * Then 노선에 존재하지 않기 때문에 조회할 수 없다.
     */

    @DisplayName("출발역과 도착역이 존재하지 않으면 조회할 수 없다.")
    @Test
    void canNotSearchWhenSourceTargetNotFound() {
        //Given
        Long 마곡역 = 지하철역_생성_요청("마곡역").jsonPath().getLong("id");

        //When
        ExtractableResponse<Response> response = 출발역_도착역_사이_경로_및_최단거리_조회(교대역, 마곡역);

        //Then
        최단거리를_조회할_수_없다(response);
    }


}
