package nextstep.subway.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.helper.JsonPathUtils.getListPath;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 조회 기능")
@AcceptanceTest
public class PathAcceptanceTest {

    /**
     * given 출발역과 도착역으로
     * when 경로를 조회하면
     * then 출발역으로부터 도착역까지 경로에 있는 역 목록을 조회할 수 있다
     */
    @DisplayName("출발역과 도착역이 주어지면 경로를 조회할 수 있다")
    @Test
    void getPath() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .queryParam("source", 1L)
            .queryParam("target", 10L)
            .when().get("/paths")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getListPath(response.body(), "stations.id", String.class)).isNotEmpty();
    }

    /**
     * given, when 출발역과 도착역이 동일하면
     * then 경로 조회에 실패한다
     */
    @DisplayName("주어진 출발역과 도착역이 같으면 경로를 조회할 수 없다")
    @Test
    void cannotGetPathIfEqualsSourceAndTarget() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .queryParam("source", 1L)
            .queryParam("target", 1L)
            .when().get("/paths")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given 출발역과 도착역이 주어졌지만
     * when 출발역과 도착역이 연결되어있지 않으면
     * then 경로 조회에 실패한다
     */
    @DisplayName("주어진 출발역과 도착역이 같으면 경로를 조회할 수 없다")
    @Test
    void cannotGetPathIfNotConnected() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .queryParam("source", 1L)
            .queryParam("target", 15L)
            .when().get("/paths")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given 주어진 출발역 혹은 도착역이
     * when 존재하지 않으면
     * then 경로 조회에 실패한다
     */
    @DisplayName("주어진 출발역과 도착역이 같으면 경로를 조회할 수 없다")
    @Test
    void cannotGetPathIfNotFound() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .queryParam("source", 1L)
            .queryParam("target", 15L)
            .when().get("/paths")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
