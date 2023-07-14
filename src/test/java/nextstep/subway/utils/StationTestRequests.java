package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import nextstep.subway.station.controller.dto.StationResponse;

public class StationTestRequests {

    public static ExtractableResponse<Response> 지하철_역_등록(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_삭제(Long id) {
        String pathVariable = "/" + id;
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations" + pathVariable)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_조회() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }


    public static Long 지하철_역_등록_Id_획득(String name) {
        return 지하철_역_등록(name).jsonPath().getLong("id");
    }


    public static List<StationResponse> 지하철_역_리스트_반환() {
        return 지하철_역_조회().jsonPath().getList("", StationResponse.class);
    }
}
