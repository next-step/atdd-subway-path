package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청한다;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.utils.RestAssuredCRUD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class PathAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> response;
    private Long 없는역Id = Long.MAX_VALUE;

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private LineResponse 칠호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남강역;
    private StationResponse 대교역;
    private StationResponse 널미터부남역;
    private StationResponse 남부터미널역;
    private StationResponse 노원역;
    private StationResponse 군자역;

    /**
     * 교대역    --- *2호선* ---     강남역 --- *2호선* ---   대교역
     * |            30                |         11
     * |                         *신분당선* 17
     * |                              |
     * |                            남강역
     * |                              |
     * *3호선* 43                 *신분당선* 10
     * |                              |
     * 남부터미널역  --- *3호선* ---   양재   --- *3호선* --- 널미터부남역
     *                  2                       21
     *
     *          (연결안됨)
     * 노원역 --- *7호선* --- 군자역
     *             77
     */

    @BeforeEach
    public void setUp() {
        super.setUp();

        given_노선이_등록되어_있다: {
            강남역 = 지하철역_생성_요청한다(StationRequest.of("강남역")).as(StationResponse.class);
            양재역 = 지하철역_생성_요청한다(StationRequest.of("양재역")).as(StationResponse.class);
            교대역 = 지하철역_생성_요청한다(StationRequest.of("교대역")).as(StationResponse.class);
            남강역 = 지하철역_생성_요청한다(StationRequest.of("남강역")).as(StationResponse.class);
            대교역 = 지하철역_생성_요청한다(StationRequest.of("대교역")).as(StationResponse.class);
            노원역 = 지하철역_생성_요청한다(StationRequest.of("노원역")).as(StationResponse.class);
            군자역 = 지하철역_생성_요청한다(StationRequest.of("군자역")).as(StationResponse.class);
            널미터부남역 = 지하철역_생성_요청한다(StationRequest.of("널미터부남역")).as(StationResponse.class);
            남부터미널역 = 지하철역_생성_요청한다(StationRequest.of("남부터미널역")).as(StationResponse.class);

            신분당선 = 지하철_노선_생성_요청(LineRequest.of("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 27)).as(LineResponse.class);
            이호선 = 지하철_노선_생성_요청(LineRequest.of("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 30)).as(LineResponse.class);
            삼호선 = 지하철_노선_생성_요청(LineRequest.of("삼호선", "bg-red-600", 교대역.getId(), 양재역.getId(), 5)).as(LineResponse.class);
            칠호선 = 지하철_노선_생성_요청(LineRequest.of("칠호선", "bg-red-600", 노원역.getId(), 군자역.getId(), 77)).as(LineResponse.class);

            지하철_노선에_지하철_구간_생성_요청(삼호선.getId(), SectionRequest.of(교대역.getId(), 남부터미널역.getId(), 43));
            지하철_노선에_지하철_구간_생성_요청(신분당선.getId(), SectionRequest.of(강남역.getId(), 남강역.getId(), 17));
            지하철_노선에_지하철_구간_생성_요청(이호선.getId(), SectionRequest.of(강남역.getId(), 대교역.getId(), 11));
            지하철_노선에_지하철_구간_생성_요청(삼호선.getId(), SectionRequest.of(양재역.getId(), 널미터부남역.getId(), 21));
        }
    }

    @DisplayName("출발역 ID 와 목적지역 ID 로 최단경로를 조회 성공한다.")
    @Test
    void findPath_1() {
        when_출발역과_도착역으로_최단경로를_조회한다: {
            response = 출발역과_도착역으로_최단경로를_조회한다(대교역.getId(), 널미터부남역.getId());
        }

        then_출발역과_도착역_최단경로_사이의_역들이_조회된다: {
            출발역과_도착역_최단경로_사이의_역들이_조회된다(response, Arrays.asList(대교역, 강남역, 남강역, 양재역, 널미터부남역));
            출발역과_도착역_최단경로_거리가_예상과_같다(response, 59);
        }
    }

    @DisplayName("출발역 ID 와 목적지역 ID 가 같은데 최단경로를 찾으면 실패한다.")
    @Test
    void findPath_2() {
        // when
        response = 출발역과_도착역으로_최단경로를_조회한다(널미터부남역.getId(), 널미터부남역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("출발역 ID 와 목적지역 ID 가 연결되어 있지 않은데 최단경로를 찾으면 실패한다.")
    @Test
    void findPath_3() {
        // when
        response = 출발역과_도착역으로_최단경로를_조회한다(노원역.getId(), 널미터부남역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("출발역 ID 와 목적지역 ID 가 하나라도 존재하지 않으면 최단경로를 찾으면 실패한다.")
    @Test
    void findPath_4() {
        // when
        response = 출발역과_도착역으로_최단경로를_조회한다(없는역Id, 널미터부남역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 출발역과_도착역으로_최단경로를_조회한다(Long srcStationId, Long dstStationId) {
        return RestAssuredCRUD.get("/paths?srcStationId={srcStationId}&dstStationId={dstStationId}", srcStationId, dstStationId);
    }

    private void 출발역과_도착역_최단경로_사이의_역들이_조회된다(ExtractableResponse<Response> pathResponse,
        List<StationResponse> expectedStations) {
        PathResponse path = pathResponse.as(PathResponse.class);

        List<Long> stationIds = path.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        List<Long> expectedStationIds = expectedStations.stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    private void 출발역과_도착역_최단경로_거리가_예상과_같다(ExtractableResponse<Response> pathResponse, int distance) {
        PathResponse path = pathResponse.as(PathResponse.class);

        assertThat(path.getDistance()).isEqualTo(distance);
    }
}
