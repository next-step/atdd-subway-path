package nextstep.subway.common;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineRestAssuredCRUD {

    public static ExtractableResponse<Response> createLine(String lineName, String lineColor) {

        Map<String, Object> param = new HashMap<>();
        param.put("name", lineName);
        param.put("color", lineColor);

        return RestAssured
                .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(param)
                .when()
                    .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> showLineList() {
        return RestAssured
                .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> showLine(Long id) {
        return RestAssured
                .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("id", id)
                .when()
                    .get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> modifyLine(Long id, String name, String color) {
        Map<String, Object> editParam = new HashMap<>();
        editParam.put("name", name);
        editParam.put("color", color);

        return RestAssured
                .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("id", id)
                    .body(editParam)
                .when()
                    .put("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured
                .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("id", id)
                .when()
                    .delete("/lines/{id}")
                .then().log().all()
                .extract();
    }

}
