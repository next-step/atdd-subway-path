package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import org.springframework.http.MediaType;

public class StationRequestSteps {

    public static ExtractableResponse<Response> 지하철_역_등록_됨(String name) {
        return 지하철_역_생성_요청(name);
    }

    public static ExtractableResponse<Response> 지하철_역_생성_요청(String name) {
        return RestAssured
                .given().log().all()
                .body(new StationRequest(name))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_제거_요청(String uri) {
        return RestAssured
                .given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
