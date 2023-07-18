package subway.acceptance.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class PathSteps {
    public static ExtractableResponse<Response> getShortestPath(long sourceId, long targetId) {
        UriComponents retrieveQueryWithBaseUri = UriComponentsBuilder
                .fromUriString("/path")
                .queryParam("source", sourceId)
                .queryParam("target", targetId)
                .build();
        return RestAssured.given().log().all()
                .when().get(retrieveQueryWithBaseUri.toUri())
                .then().log().all()
                .extract();
    }
}
