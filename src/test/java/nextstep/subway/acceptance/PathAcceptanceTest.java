package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.fixture.LineFixture;
import nextstep.subway.acceptance.fixture.StationFixture;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 조회 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = StationFixture.지하철역_생성_요청("교대역").as(StationResponse.class).getId();
        강남역 = StationFixture.지하철역_생성_요청("강남역").as(StationResponse.class).getId();
        양재역 = StationFixture.지하철역_생성_요청("양재역").as(StationResponse.class).getId();
        남부터미널역 = StationFixture.지하철역_생성_요청("남부터미널역").as(StationResponse.class).getId();

        이호선 = LineFixture.노선_생성_요청("2호선", "green", 10, 교대역, 강남역).as(LineResponse.class).getId();
        신분당선 = LineFixture.노선_생성_요청("신분당선", "red", 10, 강남역, 양재역).as(LineResponse.class).getId();
        삼호선 = LineFixture.노선_생성_요청("3호선", "orange", 2,  교대역, 남부터미널역).as(LineResponse.class).getId();

        LineFixture.구간_생성_요청(삼호선, 양재역, 남부터미널역, 3);
    }

    /**
     * When 노선의 경로를 조회하면
     * Then 출발역과 도착역 사이의 역 목록과 구간의 거리가 조회된다.
     */
    @DisplayName("경로를 조회한다.")
    @Test
    void 경로_조회_성공() {
        // when
        ExtractableResponse<Response> 경로_조회_응답 = getPaths(교대역, 양재역);

        // then
        assertThat(경로_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(경로_조회_응답.jsonPath().getList("stations.id")).containsExactly(교대역, 남부터미널역, 양재역);
    }

    /**
     * When 노선의 경로를 조회하면
     * Then 출발역과 도착역이 같은 경우 에러가 발생한다.
     */
    @DisplayName("출발역과 도착역이 동일하면 경로를 조회할 수 없다.")
    @Test
    void 출발역_도착역_동일_경로_조회_실패() {
        // when
        ExtractableResponse<Response> 경로_조회_응답 = getPaths(교대역, 교대역);

        // then
        assertThat(경로_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 노선의 경로를 조회하면
     * Then 출발역과 도착역이 연결되어 있지 않은 경우 에러가 발생한다.
     */
    @DisplayName("출발역과 도착역이 연결되어 있지 않은 경우 경로를 조회할 수 없다.")
    @Test
    void 출발역_도착역_미연결_경로_조회_실패() {
        // given
        Long 역삼역 = StationFixture.지하철역_생성_요청("역삼역").as(StationResponse.class).getId();

        // when
        ExtractableResponse<Response> 경로_조회_응답 = getPaths(교대역, 역삼역);

        // then
        assertThat(경로_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 노선의 경로를 조회하면
     * Then 존재하지 않는 출발역이나 도착역인 경우 에러가 발생한다.
     */
    @DisplayName("존재하지 않는 출발역이나 도착역인 경우 경로를 조회할 수 없다.")
    @Test
    void 존재하지_않는_출발역_도착역_경로_조회_실패() {
        // given
        Long 존재하지_않는_역 = -9999L;

        // when
        ExtractableResponse<Response> 경로_조회_응답 = getPaths(교대역, 존재하지_않는_역);

        // then
        assertThat(경로_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> getPaths(Long source, Long target) {
        return RestAssured
            .given()
            .queryParam("source", source)
            .queryParam("target", target)
            .when()
            .get("/paths")
            .then()
            .extract();
    }
}
