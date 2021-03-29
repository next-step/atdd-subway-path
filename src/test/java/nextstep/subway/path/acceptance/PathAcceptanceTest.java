package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {
    private StationResponse 방배역;
    private StationResponse 서초역;
    private StationResponse 교대역;
    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 양재역;
    private StationResponse 양재시민의숲역;
    private StationResponse 청계산입구역;
    private StationResponse 판교역;
    private StationResponse 광교역;
    private StationResponse 동천역;


    private LineResponse 이호선;
    private LineResponse 신분당선;

    private static final int DEFAULT_LINE_DISTANCE = 10;

    @BeforeEach
    public void setUp() {
        super.setUp();

        역_생성();

        이호선_생성();
        신분당선_생성();

        이호선_구간_등록();

        신분당선_구간_등록();
    }

    private void 신분당선_구간_등록() {
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 양재시민의숲역, DEFAULT_LINE_DISTANCE);
        지하철_노선에_지하철역_등록_요청(신분당선, 양재시민의숲역, 청계산입구역, DEFAULT_LINE_DISTANCE);
        지하철_노선에_지하철역_등록_요청(신분당선, 청계산입구역, 판교역, DEFAULT_LINE_DISTANCE);
    }

    private void 이호선_구간_등록() {
        지하철_노선에_지하철역_등록_요청(이호선, 서초역, 교대역, DEFAULT_LINE_DISTANCE);
        지하철_노선에_지하철역_등록_요청(이호선, 교대역, 강남역, DEFAULT_LINE_DISTANCE);
        지하철_노선에_지하철역_등록_요청(이호선, 강남역, 역삼역, DEFAULT_LINE_DISTANCE);
    }

    private void 신분당선_생성() {
        Map<String, Object> line2RequestParam = createLineParams("신분당선", "red", 강남역.getId(), 양재역.getId(), DEFAULT_LINE_DISTANCE);
        신분당선 = 지하철_노선_등록되어_있음(line2RequestParam).as(LineResponse.class);
    }

    private void 이호선_생성() {
        Map<String, Object> lineRequestParam = createLineParams("이호선", "green", 방배역.getId(), 서초역.getId(), DEFAULT_LINE_DISTANCE);
        이호선 = 지하철_노선_등록되어_있음(lineRequestParam).as(LineResponse.class);
    }

    private void 역_생성() {
        //역 생성
        방배역 = 지하철역_등록되어_있음("방배역").as(StationResponse.class);
        서초역 = 지하철역_등록되어_있음("서초역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        역삼역 = 지하철역_등록되어_있음("역삼역").as(StationResponse.class);

        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        양재시민의숲역 = 지하철역_등록되어_있음("양재시민의숲역").as(StationResponse.class);
        청계산입구역 = 지하철역_등록되어_있음("청계산입구역").as(StationResponse.class);
        판교역 = 지하철역_등록되어_있음("판교역").as(StationResponse.class);
        광교역 = 지하철역_등록되어_있음("광교역").as(StationResponse.class);

        동천역 = StationResponse.of(new Station(100L, "동천역"));
    }

    @DisplayName("역에서 역 경로를 조회하는데 등록되지 않은 시작 역이면 오류")
    @Test
    public void finePathWithNotRegisteredSource() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(동천역, 판교역);

        // then
        경로_조회_응답실패(response);
    }

    @DisplayName("역에서 역 경로를 조회하는데 등록되지 않은 도착 역이면 오류")
    @Test
    public void finePathWithNotRegisteredTarget() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(서초역, 동천역);

        // then
        경로_조회_응답실패(response);
    }

    @DisplayName("시작역과 도착역을 동일하게 넣으면 오류")
    @Test
    public void finePathWithSameSourceIdAndTargetId() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(서초역, 서초역);

        // then
        경로_조회_응답실패(response);
    }

    @DisplayName("역이 연결되어있지 않으면 오류")
    @Test
    public void findPathWithNotConnectedStations() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(서초역, 광교역);

        // then
        경로_조회_응답실패(response);
    }

    @DisplayName("경로를 조회한다.")
    @Test
    void getPath() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(서초역, 판교역);

        // then
        경로_조회_응답(response);
        경로_조회_응답_DISTANCE_확인_성공(6*DEFAULT_LINE_DISTANCE, response);
    }

    public static ExtractableResponse<Response> 지하철_경로_조회_요청(StationResponse sourceStation, StationResponse targetStation) {
        return RestAssured.given().log().all().
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/paths?source="+sourceStation.getId()+"&target="+targetStation.getId()).
                then().
                log().all().
                extract();
    }

    public void 경로_조회_응답(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(PathResponse.class)).isNotNull();
    }

    public void 경로_조회_응답실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public void 경로_조회_응답_DISTANCE_확인_성공(int distance, ExtractableResponse<Response> response) {
        PathResponse pathResponse = response.as(PathResponse.class);

        assertThat(pathResponse.getDistance()).isEqualTo(distance);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(PathResponse.class)).isNotNull();
    }
}
