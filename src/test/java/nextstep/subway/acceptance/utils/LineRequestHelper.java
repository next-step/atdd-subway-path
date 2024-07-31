package nextstep.subway.acceptance.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.RestAssured;
import org.springframework.http.MediaType;
import nextstep.subway.dto.SubwayLineRequest;
import nextstep.subway.dto.SubwayLineResponse;
import nextstep.subway.dto.SubwayLineUpdateRequest;

import java.util.List;

public class LineRequestHelper {
    public static ResponseHelper<List<SubwayLineResponse>> requestGetAll() {
        var response = RestAssured
                .when().get("/lines")
                .then()
                .extract();
        return new ResponseHelper<>(response, new TypeReference<>() {
        });
    }

    public static ResponseHelper<SubwayLineResponse> requestGet(Long id) {
        var response = RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .get("/lines/{id}")
                .then()
                .log().all()
                .extract();
        return new ResponseHelper<>(response, new TypeReference<>() {
        });
    }

    public static ResponseHelper<Void> requestDelete(Long id) {
        var response = RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .delete("/lines/{id}")
                .then()
                .extract();
        return new ResponseHelper<>(response, new TypeReference<>() {
        });
    }

    public static ResponseHelper<SubwayLineResponse> requestCreate(SubwayLineRequest request) {
        var contentType = MediaType.APPLICATION_JSON_VALUE;
        var path = "/lines";

        var response = RestAssured.given()
                .body(request)
                .contentType(contentType)
                .when().post(path)
                .then().log().all()
                .extract();
        return new ResponseHelper<>(response, new TypeReference<>() {
        });

    }

    public static ResponseHelper<Void> requestUpdate(Long id, SubwayLineUpdateRequest request) {
        var contentType = MediaType.APPLICATION_JSON_VALUE;

        var response = RestAssured
                .given()
                .pathParam("id", id)
                .body(request)
                .contentType(contentType)
                .when()
                .put("/lines/{id}")
                .then()
                .extract();
        return new ResponseHelper<>(response, new TypeReference<>() {
        });
    }
}
