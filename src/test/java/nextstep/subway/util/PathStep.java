package nextstep.subway.util;

import io.restassured.RestAssured;
import nextstep.subway.common.dto.ErrorResponse;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.MediaType;

public class PathStep {

    public static PathResponse 경로_조회(Long source, Long target) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source=" + source + "&target=" + target)
                .then().log().all()
                .extract().response().body().as(PathResponse.class);
    }

    public static ErrorResponse 경로_조회_실패(Long source, Long target) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source=" + source + "&target=" + target)
                .then().log().all()
                .extract().response().body().as(ErrorResponse.class);
    }
}
