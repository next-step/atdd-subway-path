package nextstep.subway.utils;

import io.restassured.RestAssured;
import nextstep.subway.domain.response.PathResponse;

import java.util.HashMap;
import java.util.Map;

public class PathTestUtil {

    public static PathResponse getShortestPath(Long sourceId, Long targetId) {
        Map<String, String> params = new HashMap<>();
        params.put("source", sourceId.toString());
        params.put("target", targetId.toString());

        return RestAssured.given().log().all()
                .queryParams(params)
                .when().get("/paths")
                .then().log().all()
                .extract()
                .as(PathResponse.class);
    }

}
