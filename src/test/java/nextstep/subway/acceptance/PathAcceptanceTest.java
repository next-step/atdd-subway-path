package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.AssertUtils;
import nextstep.subway.utils.ResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_경로_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

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
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = ResponseUtils.getLong(지하철역_생성_요청("교대역"), "id");
        강남역 = ResponseUtils.getLong(지하철역_생성_요청("강남역"), "id");
        양재역 = ResponseUtils.getLong(지하철역_생성_요청("양재역"), "id");
        남부터미널역 = ResponseUtils.getLong(지하철역_생성_요청("남부터미널역"), "id");

        이호선 = ResponseUtils.getLong(지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10), "id");
        신분당선 = ResponseUtils.getLong(지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10), "id");
        삼호선 = ResponseUtils.getLong(지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2), "id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * Given 출발역과 도착역을 지정하고
     * When 역을 전달하여 지하철 경로를 조회하면
     * Then 지하철 경로와 거리를 확인할 수 있다.
     */
    @DisplayName("지하철 경로 조회")
    @Test
    void findPath() {
        // given
        Long source = 교대역;
        Long target = 양재역;

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회(source, target);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(ResponseUtils.getLongList(response, "stations.id")).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(ResponseUtils.getInt(response, "distance")).isEqualTo(5);
    }

    /**
     * When 같은 지하철 역을 전달하여 지하철 경로를 조회하면
     * Then 예외가 발생한다.
     */
    @DisplayName("출발역과 도착역이 같은 경우 예외 발생")
    @Test
    void findPathSameStation() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회(교대역, 교대역);

        // then
        AssertUtils.badRequest(response);
    }

    /**
     * Given 기존 지하철과 연결되지 않는 새로운 지하철 노선을 추가하고
     * When 기존 지하철역과 새로 추가된 지하철역의 지하철 경로를 조회하면
     * Then 예외가 발생한다.
     */
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 예외 발생")
    @Test
    void findPathNotConnectStation() {
        // given
        Long 서울역 = ResponseUtils.getLong(지하철역_생성_요청("서울역"), "id");
        Long 시청역 = ResponseUtils.getLong(지하철역_생성_요청("시청역"), "id");
        지하철_노선_생성_요청("1호선", "blue", 서울역, 시청역, 3);

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회(교대역, 시청역);

        // then
        AssertUtils.badRequest(response);
    }

    /**
     * Given 새로운 지하철역을 추가하고
     * When 새로 추가된 지하철역을 출발역, 기존 지하철역을 도착역으로 전달하여 지하철 경로를 조회하면
     * Then 예외가 발생한다.
     */
    @DisplayName("노선에 존재하지 않는 지하철역을 지하철 경로 조회하면 예외 발생")
    @Test
    void findPathUnknownStation() {
        // given
        Long 서울역 = ResponseUtils.getLong(지하철역_생성_요청("서울역"), "id");

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회(교대역, 서울역);

        // then
        AssertUtils.badRequest(response);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        return Map.of(
                "upStationId", upStationId.toString(),
                "downStationId", downStationId.toString(),
                "distance", String.valueOf(distance)
        );
    }
}
