package nextstep.subway.common;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathRestAssuredCRUD {

    public static ExtractableResponse<Response> showPath(Long sourceId, Long targetId) {
        return RestAssured
                .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("source", sourceId)
                    .param("target", targetId)
                .when()
                    .get("/path")
                .then().log().all()
                .extract();
    }
}
