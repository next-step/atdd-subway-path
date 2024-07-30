package nextstep.subway.acceptance.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.RestAssured;
import org.springframework.http.MediaType;
import nextstep.subway.dto.StationCreateRequest;
import nextstep.subway.dto.StationResponse;

import java.util.List;

public class StationRequestHelper {

    public static ResponseHelper<List<StationResponse>> requestGetAll() {
        var response = RestAssured
                .when().get("/stations")
                .then()
                .extract();
        return new ResponseHelper<>(response, new TypeReference<>() {
        });

    }

    public static ResponseHelper<Void> requestDelete(Long id) {
        var response = RestAssured.given()
                .pathParam("id", id)
                .when()
                .delete("/stations/{id}")
                .then()
                .extract();
        return new ResponseHelper<>(response, new TypeReference<>() {
        });
    }

    public static ResponseHelper<StationResponse> requestCreate(StationCreateRequest request) {
        var contentType = MediaType.APPLICATION_JSON_VALUE;
        var path = "/stations";

        var response = RestAssured.given().log().all()
                .body(request)
                .contentType(contentType)
                .when().post(path)
                .then().log().all()
                .extract();
        return new ResponseHelper<>(response, new TypeReference<>() {
        });
    }
}
