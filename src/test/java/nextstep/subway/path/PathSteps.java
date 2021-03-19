package nextstep.subway.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {

    public static ExtractableResponse<Response> 최단경로_요청(int source, int target){
        return RestAssured.given()
                .queryParams("source", source,
                        "target", target)
                .log().all()
                .when().get("/paths")
                .then().log().all()
                .extract();
    }

    public static void 최단거리_확인(ExtractableResponse<Response> response, int distance){
        assertThat(response.jsonPath().getObject("distance", Integer.class)).isEqualTo(distance);
    }

    public static void 최단경로_확인(ExtractableResponse<Response> response, List<String> toBeList){
        List<String> expectedList = response.jsonPath()
                .getList("stations", StationResponse.class)
                .stream().map(str -> str.getName())
                .collect(Collectors.toList());

        assertThat(expectedList)
                .containsExactlyElementsOf(toBeList);
    }
}
