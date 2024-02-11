package nextstep.subway.path;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.StationResponse;
import nextstep.subway.testhelper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {

    private StationFixture stationFixture;
    private Long 잠실역_ID;
    private Long 강남역_ID;
    private Long 삼성역_ID;
    private Long 선릉역_ID;
    private Long 일호선_잠실역_부터_강남역_ID;
    private Long 이호선_강남역_부터_삼성역_ID;
    private Long 삼호선_강남역_부터_선릉역_ID;

    @BeforeEach
    public void setUp() {
        super.setUp();
        stationFixture = new StationFixture();
        잠실역_ID = stationFixture.get잠실역_ID();
        강남역_ID = stationFixture.get강남역_ID();
        삼성역_ID = stationFixture.get삼성역_ID();
        선릉역_ID = stationFixture.get선릉역_ID();

        LineFixture lineFixture = new LineFixture(stationFixture);
        일호선_잠실역_부터_강남역_ID = JsonPathHelper.getObject(LineApiCaller.지하철_노선_생성(lineFixture.get일호선_잠실역_부터_강남역_params()), "id", Long.class);
        이호선_강남역_부터_삼성역_ID = JsonPathHelper.getObject(LineApiCaller.지하철_노선_생성(lineFixture.get이호선_강남역_부터_삼성역_params()), "id", Long.class);
        삼호선_강남역_부터_선릉역_ID = JsonPathHelper.getObject(LineApiCaller.지하철_노선_생성(lineFixture.get삼호선_잠실역_부터_선릉역_params()), "id", Long.class);

        SectionFixture sectionFixture = new SectionFixture(stationFixture);
        LineApiCaller.지하철_노선에_구간_추가(sectionFixture.get선릉역_부터_삼성역_구간_params(), "/lines/" + 삼호선_강남역_부터_선릉역_ID.toString());
    }

    /**
     * GIVEN 지하철 노선들을 생성하고 구간을 추가 후</br>
     * WHEN 출발역과 도착역을 입력하면</br>
     * THEN 구간과 거리를 알 수 있다
     */
    @Test
    @DisplayName("출발역과 도착역을 입력하면 구간과 거리를 알 수 있다")
    void findPath1() {
        // given
        // when
        PathResponse pathResponse = JsonPathHelper.getObject(PathApiCaller.경로_조회(new PathRequest(잠실역_ID, 삼성역_ID)), ".",
                PathResponse.class);

        // then
        List<StationResponse> actualStations = pathResponse.getStations();
        StationResponse[] expectedStations = {new StationResponse(잠실역_ID, "잠실역"),
                new StationResponse(선릉역_ID, "선릉역"),
                new StationResponse(삼성역_ID, "삼성역")};
        assertThat(actualStations).containsExactly(expectedStations);

        Long actualDistance = pathResponse.getDistance();
        Long expectedDistance = 5L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    /**
     * GIVEN 지하철 노선들을 생성하고 구간을 추가 후
     * WHEN 출발역과 도착역을 같게 입력하면
     * THEN 에러 처리와 함께 '출발역과 도착역은 같을 수 없습니다.' 라는 메세지가 출력된다
     */
    @Test
    @DisplayName("동일한 출발역과 도착역을 입력하면 '출발역과 도착역은 같을 수 없습니다.' 라는 메세지가 출력된다")
    void findPath2() {
        // given
        // when
        PathRequest pathRequest = new PathRequest(잠실역_ID, 잠실역_ID);
        ExtractableResponse<Response> response = given().log().all()
                .queryParam("source", pathRequest.getSource())
                .queryParam("target", pathRequest.getTarget())
                .when().get("/path")
                .then().log().all()
                .extract();

        // then
        int actual = response.statusCode();
        int expected = HttpStatus.BAD_REQUEST.value();
        assertThat(actual).isEqualTo(expected);

        String actualBody = response.asString();
        String expectedBody = "출발역과 도착역은 같을 수 없습니다.";
        assertThat(actualBody).isEqualTo(expectedBody);
    }

    /**
     * GIVEN 지하철 노선들을 생성하고 구간을 추가 후
     * WHEN 출발역과 도착역이 연결이 되지 않게 입력하면
     * THEN 에러 처리와 함께 '출발역과 도착역은 연결되어 있어야 합니다.' 라는 메세지가 출력된다
     */
    @Test
    @DisplayName("연결이 안된 출발역과 도착역을 입력하면 '출발역과 도착역은 연결되어 있어야 합니다.' 라는 메세지가 출력된다")
    void findPath3() {
        // given
        // when
        PathRequest pathRequest = new PathRequest(강남역_ID, 삼성역_ID);
        ExtractableResponse<Response> response = given().log().all()
                .queryParam("source", pathRequest.getSource())
                .queryParam("target", pathRequest.getTarget())
                .when().get("/path")
                .then().log().all()
                .extract();

        // then
        int actual = response.statusCode();
        int expected = HttpStatus.BAD_REQUEST.value();
        assertThat(actual).isEqualTo(expected);

        String actualBody = response.asString();
        String expectedBody = "출발역과 도착역은 연결되어 있어야 합니다.";
        assertThat(actualBody).isEqualTo(expectedBody);
    }

    /**
     * GIVEN 지하철 노선들을 생성하고 구간을 추가 후
     * WHEN 존재 하지 않는 역을 입력하면
     * THEN 에러 처리와 함께 '입력한 역을 찾을 수 없습니다.' 라는 메세지가 출력된다
     */
    @Test
    @DisplayName("등록이 안된 출발역과 도착역을 입력하면 '입력한 역을 찾을 수 없습니다.' 라는 메세지가 출력된다")
    void findPath4() {
        // given
        // when
        PathRequest pathRequest = new PathRequest(stationFixture.get교대역_ID(), stationFixture.get서초역_ID());
        ExtractableResponse<Response> response = given().log().all()
                .queryParam("source", pathRequest.getSource())
                .queryParam("target", pathRequest.getTarget())
                .when().get("/path")
                .then().log().all()
                .extract();

        // then
        int actual = response.statusCode();
        int expected = HttpStatus.BAD_REQUEST.value();
        assertThat(actual).isEqualTo(expected);

        String actualBody = response.asString();
        String expectedBody = "입력한 역을 찾을 수 없습니다.";
        assertThat(actualBody).isEqualTo(expectedBody);
    }

}
