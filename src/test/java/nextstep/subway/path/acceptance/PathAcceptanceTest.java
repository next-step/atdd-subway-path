package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.acceptance.LineSectionAcceptanceTest;
import nextstep.subway.line.acceptance.LineSteps;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로탐색 인수테스트")
public class PathAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 삼성역;
    private StationResponse 잠실역;
    private StationResponse 고속터미널역;
    private StationResponse 강남구청역;
    private StationResponse 판교역;
    private StationResponse 야탑역;

    private LineResponse 이호선;
    private LineResponse 삼호선;
    private LineResponse 칠호선;
    private LineResponse 분당선;

    @BeforeEach
    void before() {
        강남역 = 지하철역_생성_요청("강남역");
        삼성역 = 지하철역_생성_요청("삼성역");
        잠실역 = 지하철역_생성_요청("잠실역");
        고속터미널역 = 지하철역_생성_요청("고속터미널역");
        강남구청역 = 지하철역_생성_요청("강남구청역");
        판교역 = 지하철역_생성_요청("판교역");
        야탑역 = 지하철역_생성_요청("야탑역");

        LineRequest request2line = new LineRequest("이호선", "green", 강남구청역.getId(), 잠실역.getId(),40);
        LineRequest request3line = new LineRequest("삼호선", "red", 강남역.getId(), 고속터미널역.getId(),5);
        LineRequest request7line = new LineRequest("칠호선", "grey", 고속터미널역.getId(), 강남구청역.getId(),10);
        LineRequest requestLine = new LineRequest("분당선", "yellow", 판교역.getId(), 야탑역.getId(),30);

        이호선 = 노선_생성_요청(request2line);
        삼호선 = 노선_생성_요청(request3line);
        칠호선 = 노선_생성_요청(request7line);
        분당선 = 노선_생성_요청(requestLine);

        지하철노선에_지하철역_등록_요청(이호선, 잠실역, 삼성역, 5);
        지하철노선에_지하철역_등록_요청(이호선, 삼성역, 강남역, 5);
    }

    /**
     * 야탑역    ---*분당선*---    판교역
     *
     * 고속터미널역           --- *7호선* ---             강남구청역
     * |                                                      |
     * *3호선*                                            *2호선*
     * |                                                    |
     * 강남역 --- *2호선*  --- 삼성역  --- *2호선* ---   잠실역
     */

    @DisplayName("두개의 역 간의 최단거리를 조회한다.")
    @Test
    void searchShortPath() {
        //when
        ExtractableResponse<Response> response = 노선에_지하철역_최단_경로_요청(삼성역.getId(), 강남구청역.getId());

        //then
        PathResponse pathResponse = 최단경로_요청_응답됨(response);

        최단경로_지하철역_개수같음(pathResponse, 4);
        최단경로_요청결과_순서_같음(pathResponse, Arrays.asList(삼성역, 강남역, 고속터미널역, 강남구청역));
        최단경로_거리_같음(pathResponse, 20);
    }

    @DisplayName("출발역과 도착역이 같은경우 검색은 실패한다.")
    @Test
    void searchPathFinderWithDuplicatePoints() {
        //when
        ExtractableResponse<Response> response = 노선에_지하철역_최단_경로_요청(삼성역.getId(), 삼성역.getId());

        //then
        최단_경로_요청_실패됨(response);
        에러_응답_메세지_비교(response, "출발역과 도착역이 같습니다.");
    }

    @DisplayName("출발역과 도착역이 연결되어있지 않을 경우 검색은 실패한다.")
    @Test
    void searchPathFinderNotConnectedPoints() {
        //when
        ExtractableResponse<Response> response = 노선에_지하철역_최단_경로_요청(야탑역.getId(), 강남역.getId());

        //when
        최단_경로_요청_실패됨(response);
        에러_응답_메세지_비교(response, "경로가 존재하지 않습니다.");
    }

    @DisplayName("존재하지 않는 출발역이나 도착역을 조회할 경우 검색은 실패한다.")
    @Test
    void searchPathFindWithNotExistsStation() {
        //when
        ExtractableResponse<Response> response = 노선에_지하철역_최단_경로_요청(100L, 1000L);

        //then
        최단_경로_요청_실패됨(response);
        에러_응답_메세지_비교(response, "100에 해당되는 리소스를 찾지 못했습니다.");
    }

    private ExtractableResponse<Response> 노선에_지하철역_최단_경로_요청(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths" + String.format("?source=%d&target=%d", source, target))
                .then().log().all()
                .extract();
    }

    private PathResponse 최단경로_요청_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response.body().as(PathResponse.class);
    }

    private void 최단_경로_요청_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 최단경로_요청결과_순서_같음(PathResponse response, List<StationResponse> expectedStations) {
        List<Long> stationIds = response.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    private void 최단경로_지하철역_개수같음(PathResponse response, int size) {
        assertThat(response.getStations()).hasSize(size);
    }

    private void 최단경로_거리_같음(PathResponse response, int expectDistance) {
        assertThat(response.getDistance()).isEqualTo(expectDistance);
    }

    private void 에러_응답_메세지_비교(ExtractableResponse<Response> response, String message) {
        assertThat(response.asString()).isEqualTo(message);
    }

    private LineResponse 지하철노선에_지하철역_등록_요청(LineResponse line, StationResponse upStation, StationResponse downStation, int distance) {
        return LineSectionAcceptanceTest.노선에_지하철역_등록_요청(line, upStation.getId(), downStation.getId(), distance).body().as(LineResponse.class);
    }

    private StationResponse 지하철역_생성_요청(String name) {
        return StationAcceptanceTest.지하철역_생성_요청(name).body().as(StationResponse.class);
    }

    private LineResponse 노선_생성_요청(LineRequest lineRequest) {
        return LineSteps.노선_생성_요청(lineRequest).body().as(LineResponse.class);
    }
}
