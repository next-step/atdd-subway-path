package nextstep.subway.path.acceptance;

import com.google.common.collect.Lists;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.domain.PathType;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.line.acceptance.LineSteps.지하철_노선에_지하철역_등록_요청;
import static nextstep.subway.station.acceptance.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {
    private StationResponse 교대역;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 남부터미널역;
    private LineResponse 이호선;
    private LineResponse 신분당선;
    private LineResponse 삼호선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_등록되어_있음(로그인_사용자, "교대역").as(StationResponse.class);
        강남역 = 지하철역_등록되어_있음(로그인_사용자, "강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음(로그인_사용자, "양재역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음(로그인_사용자, "남부터미널역").as(StationResponse.class);

        이호선 = 지하철_노선_등록되어_있음(로그인_사용자, "2호선", "green", 교대역, 강남역, 10, 2);
        신분당선 = 지하철_노선_등록되어_있음(로그인_사용자, "신분당선", "green", 강남역, 양재역, 10, 3);
        삼호선 = 지하철_노선_등록되어_있음(로그인_사용자, "3호선", "green", 교대역, 남부터미널역, 2, 10);

        지하철_노선에_지하철역_등록_요청(로그인_사용자, 삼호선, 남부터미널역, 양재역, 3, 10);
    }

    @DisplayName("두 역의 최단 거리 경로를 조회한다.")
    @Test
    void findPathByDistance() {
        // when
        ExtractableResponse<Response> response = 두_역의_경로_조회를_요청(로그인_사용자, 교대역.getId(), 양재역.getId(), PathType.DISTANCE);

        // then
        최단_거리_경로_응답됨(response);
    }

    @DisplayName("두 역의 최소 시간 경로를 조회한다.")
    @Test
    void findPathByDuration() {
        // when
        ExtractableResponse<Response> response = 두_역의_경로_조회를_요청(로그인_사용자, 교대역.getId(), 양재역.getId(), PathType.DURATION);

        // then
        최소_시간_경로_응답됨(response);
    }

    @DisplayName("두 역의 최소 시간 경로를 조회한다.")
    @Test
    void findPathWithSameSourceAndTarget() {
        // when
        ExtractableResponse<Response> response = 두_역의_경로_조회를_요청(로그인_사용자, 교대역.getId(), 교대역.getId(), PathType.DURATION);

        // then
        경로_응답_실패됨(response);
    }

    private void 경로_응답_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private LineResponse 지하철_노선_등록되어_있음(TokenResponse user, String name, String color, StationResponse upStation, StationResponse downStation, int distance, int duration) {
        LineRequest lineRequest = new LineRequest(name, color, upStation.getId(), downStation.getId(), distance, duration);
        return 지하철_노선_생성_요청(user, lineRequest).as(LineResponse.class);
    }

    private ExtractableResponse<Response> 두_역의_경로_조회를_요청(TokenResponse user, Long source, Long target, PathType type) {
        return RestAssured.given().log().all()
                .auth().oauth2(user.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("source", source)
                .queryParam("target", target)
                .queryParam("type", type)
                .when().get("/paths")
                .then().log().all().extract();
    }

    private void 최단_거리_경로_응답됨(ExtractableResponse<Response> response) {
        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getDistance()).isEqualTo(5);

        List<Long> stationIds = pathResponse.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(Lists.newArrayList(교대역.getId(), 남부터미널역.getId(), 양재역.getId()));
    }

    private void 최소_시간_경로_응답됨(ExtractableResponse<Response> response) {
        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getDuration()).isEqualTo(5);

        List<Long> stationIds = pathResponse.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(Lists.newArrayList(교대역.getId(), 강남역.getId(), 양재역.getId()));
    }
}
