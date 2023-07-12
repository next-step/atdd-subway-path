package subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class LineSteps {

    public static ExtractableResponse<Response> 노선_생성_API(final Map<String, String> line) {
        return RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_목록_조회_API() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_조회_API(final String createdLocation) {
        return RestAssured.given().log().all()
                .when().get(createdLocation)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_수정_API(final String createdLocation, final Map<String, String> request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(createdLocation)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 구간_추가_API(final String appendLocation, final Map<String, String> request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(appendLocation)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 구간_제거_API(final String baseUri, final Long deleteLocation) {
        UriComponents deleteQueryWithBaseUri = UriComponentsBuilder
                .fromUriString(baseUri)
                .queryParam("stationId", deleteLocation)
                .build();
        return RestAssured.given().log().all()
                .when().delete(deleteQueryWithBaseUri.toUri())
                .then().log().all()
                .extract();
    }
}
