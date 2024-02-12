package nextstep.subway.utils;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathFactory {

    public static ExtractableResponse<Response> findShortestPath(Long sourceId, Long targetId) {
        return RestAssured.given().log().all()
                .queryParam("source", sourceId)
                .queryParam("target", targetId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
