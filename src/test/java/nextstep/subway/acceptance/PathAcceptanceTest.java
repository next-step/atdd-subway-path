package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.response.LineResponse;
import nextstep.subway.applicaion.dto.response.PathResponse;
import nextstep.subway.applicaion.dto.response.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.steps.LineSteps.지하철_노선_생성;
import static nextstep.subway.acceptance.steps.SectionSteps.지하철_노선_구간_등록;
import static nextstep.subway.acceptance.steps.StationSteps.지하철역_생성_응답;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    private static final String BASE_URL = "/paths";

    private static final int DEFAULT_DISTANCE = 10;
    private static final int SHORT_DISTANCE = 5;

    private LineResponse 신분당선, 이호선, 삼호선;
    private StationResponse 교대역, 강남역, 양재역, 남부터미널역;


    /**                     10
     *         교대역    --- *2호선* ---   강남역
     *          |                        |
     *   10   *3호선*                   *신분당선*  10
     *          |                        |
     *        남부터미널역  --- *3호선* ---   양재
     *                        5
     */

    @BeforeEach
    void set() {
        교대역 = 지하철역_생성_응답("교대역");
        강남역 = 지하철역_생성_응답("강남역");
        양재역 = 지하철역_생성_응답("양재역");
        남부터미널역 = 지하철역_생성_응답("남부터미널역");

        신분당선 = 지하철_노선_생성("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), DEFAULT_DISTANCE)
                .jsonPath()
                .getObject("", LineResponse.class);
        이호선 = 지하철_노선_생성("2호선", "bg-green-600", 강남역.getId(), 교대역.getId(), DEFAULT_DISTANCE)
                .jsonPath()
                .getObject("", LineResponse.class);
        삼호선 = 지하철_노선_생성("3호선", "bg-orange-600", 교대역.getId(), 남부터미널역.getId(), DEFAULT_DISTANCE)
                .jsonPath()
                .getObject("", LineResponse.class);

        지하철_노선_구간_등록(삼호선.getId(),남부터미널역.getId(),양재역.getId(),SHORT_DISTANCE);


    }

    /**
     * When 출발역과 도착역으로 경로를 조회하면
     * Then 출발역으로부터 도착역까지의 경로에 있는 역 목록과 조회한 경로 구간의 거리를 응답받는다.
     */
    @DisplayName("최단 경로 조회")
    @Test
    void getPaths() {
        Long source = 교대역.getId();
        Long target = 양재역.getId();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get(BASE_URL + "?source=" + source + "&target=" + target)
                .then().log().all()
                .extract();

        PathResponse path = response.jsonPath().getObject("", PathResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        int stationCount = path.getStations().size();
        assertThat(stationCount).isEqualTo(3);
        assertThat(path.getDistance()).isEqualTo(DEFAULT_DISTANCE+SHORT_DISTANCE);
    }

    /**
     * When 출발역과 도착역을 동일하게 입력 후 경로를 조회하면
     * Then 에러를 던진다.
     */
    @DisplayName("최단 경로 조회 - 출발역과 도착역이 같은 경우 에러를 던진다.")
    @Test
    void getPathsExceptionWhenSameSourceAndTarget() {
        Long stationId = 교대역.getId();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get(BASE_URL + "?source=" + stationId + "&target=" + stationId)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 출발역과 도착역이 연결이 되어 있지 않은 경로를 조회하면
     * Then 에러를 던진다.
     */
    @DisplayName("최단 경로 조회 - 출발역과 도착역이 연결이 되어 있지 않은 경우 에러를 던진다.")
    @Test
    void getPathsExceptionWhenNotConnectedPath() {
        //given
        Long 신도림역_아이디 = 지하철역_생성_응답("신도림역").getId();
        Long 용산역_아이디 = 지하철역_생성_응답("용산역").getId();
        LineResponse 일호선 = 지하철_노선_생성("1호선", "bg-blue-600", 신도림역_아이디, 용산역_아이디, DEFAULT_DISTANCE)
                .jsonPath()
                .getObject("", LineResponse.class);

        //when
        Long source = 교대역.getId();
        Long target = 신도림역_아이디;
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get(BASE_URL + "?source=" + source + "&target=" + target)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * When 존재하지 않은 출발역이나 도착역을 조회하면
     * Then 에러를 던진다.
     */
    @DisplayName("최단 경로 조회 - 존재하지 않은 출발역이나 도착역을 조회 할 경우 에러를 던진다.")
    @Test
    void getPathsExceptionWhenNotExistedStation() {
        Long stationId = 교대역.getId();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get(BASE_URL + "?source=" + stationId + "&target=" + Integer.MIN_VALUE)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
