package nextstep.util;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class RestAssuredUtils {

    public static void ETag를_검증한다(String uri, ExtractableResponse<Response> response) {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.IF_NONE_MATCH, response.header(HttpHeaders.ETAG))
                .get(uri)
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_MODIFIED.value())
                .header(HttpHeaders.ETAG, Matchers.notNullValue(String.class));
    }
}
