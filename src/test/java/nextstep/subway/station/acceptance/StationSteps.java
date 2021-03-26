package nextstep.subway.station.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class StationSteps {
    public static final String 강남역 = "강남역";
    public static final String 역삼역 = "역삼역";

    public static ExtractableResponse<Response> 지하철역_등록되어_있음(TokenResponse user, String name) {
        ExtractableResponse<Response> stationResponse = 지하철역_생성_요청(user, name);
        assertThat(stationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return stationResponse;
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(TokenResponse user, String name) {
        StationRequest stationRequest = new StationRequest(name);

        return RestAssured
                .given().log().all()
                .auth().oauth2(user.getAccessToken())
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회_요청(TokenResponse user) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(user.getAccessToken())
                .when()
                .get("/stations")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철역_수정_요청(TokenResponse user, ExtractableResponse<Response> response, StationRequest stationRequest) {
        String uri = response.header("Location");

        return RestAssured
                .given().log().all()
                .auth().oauth2(user.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest)
                .when().put(uri)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철역_제거_요청(TokenResponse user, ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured
                .given().log().all()
                .auth().oauth2(user.getAccessToken())
                .when().delete(uri)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철역_제거_요청(TokenResponse user, StationResponse stationResponse) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(user.getAccessToken())
                .when().delete("/stations/" + stationResponse.getId())
                .then().log().all().extract();
    }
}
