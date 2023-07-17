package nextstep.subway.acceptance.commonStep;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineStep {

    public static ExtractableResponse<Response> 지하철_노선_생성(String lineName,String color){


        Map<String, String> params = new HashMap<>();
        params.put("name",lineName);
        params.put("color",color);


        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/lines")
                        .then().log().all()
                        .extract();

        return response;
    }

    public static List<String> 지하철노선_목록_전체조회() {

        List<String> lineNames =
                RestAssured.given()
                        .when().get("/lines")
                        .then()
                        .extract().jsonPath().getList("name", String.class);

        return lineNames;
    }

    public static JsonPath 지하철_노선_조회(Long lineId){

        JsonPath result =
                RestAssured.given().log().all()
                        .when().get("/lines/"+lineId)
                        .then().log().all()
                        .extract().jsonPath();

        return result;
    }
}
