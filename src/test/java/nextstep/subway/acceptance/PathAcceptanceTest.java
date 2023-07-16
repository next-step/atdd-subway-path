package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.utils.AbstractAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 기능 인수 테스트")
public class PathAcceptanceTest extends AbstractAcceptanceTest {

    private String 교대역명 = "교대역";
    private String 강남역명 = "강남역";
    private String 양재역명 = "양재역명";
    private String 남부터미널역명 = "남부터미널역";

    private Long 교대역_id;
    private Long 강남역_id;
    private Long 양재역_id;
    private Long 남부터미널역_id;

    private Long 이호선_id;
    private Long 신분당선_id;
    private Long 삼호선_id;

    /**
     * 교대역  ---- *2호선* --- d:10 ------  강남역
     * |                                    |
     * *3호선*                            *신분당선*
     * d:2                                 d:10
     * |                                   |
     * 남부터미널역  --- *3호선* -- d:3 --- 양재
     */

    @BeforeEach
    public void setUp() {
        교대역_id = StationSteps.지하철역_생성_요청(교대역명);
        강남역_id = StationSteps.지하철역_생성_요청(강남역명);
        양재역_id = StationSteps.지하철역_생성_요청(양재역명);
        남부터미널역_id = StationSteps.지하철역_생성_요청(남부터미널역명);

        이호선_id = LineSteps.지하철_노선_생성_요청("2호선", 교대역_id, 강남역_id, 10);
        신분당선_id = LineSteps.지하철_노선_생성_요청("신분당선", 강남역_id, 양재역_id, 10);
        삼호선_id = LineSteps.지하철_노선_생성_요청("3호선", 교대역_id, 남부터미널역_id, 2);

        LineSteps.지하철_노선_구간_등록_요청(삼호선_id, new SectionRequest(남부터미널역_id, 양재역_id, 3));
    }

    /**
     * Given: 지하철 노선과 구간을 생성한다.
     * When: 출발역과 도착역을 정하고 경로를 찾는다.
     * Then: 찾은 경로의 출발역과 도착역은 입력한 출발역, 도착역과 일치한다.
     * Then: 찾은 경로는 가능한 경로 중에서 거리가 제일 짧다.
     */
    @Test
    void 최단_경로_조회() {
        //when
        ExtractableResponse<Response> response = PathSteps.지하철_경로_조회(교대역_id, 양재역_id);

        //then
        List<String> stations = response.jsonPath().get("stations.name");
        assertThat(stations).startsWith(교대역명).endsWith(양재역명);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
    }

    /**
     * Given: 지하철 노선과 구간을 생성한다.
     * When: 출발역과 도착역이 같은 경로를 찾는다.
     * Then: 예외를 발생한다.
     */
    @Test
    void 출발역과_도착역이_같은_경우() {
        //when
        ExtractableResponse<Response> response = PathSteps.지하철_경로_조회(교대역_id, 교대역_id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given: 지하철 노선과 구간을 생성한다.
     * When: 존재하지 않는 출발역과 존재하는 도착역의 경로를 찾는다.
     * Then: 예외를 발생한다.
     */
    @Test
    void 존재하지_않는_출발역인_경우() {
        //given

        //when

        //then
    }

    /**
     * Given: 지하철 노선과 구간을 생성한다.
     * When: 존재하는 출발역과 존재하지 않는 도착역의 경로를 찾는다.
     * Then: 예외를 발생한다.
     */
    @Test
    void 존재하지_않는_도착역인_경우() {
        //given

        //when

        //then
    }

    /**
     * Given: 지하철 노선과 구간을 생성한다.
     * When: 연결되어 있지 않는 출발역과 도착역의 경로를 찾는다.
     * Then: 예외를 발생한다.
     */
    @Test
    void 연결되어_있지_않는_출발역과_도착역인_경우() {
        //given

        //when

        //then
    }
}
