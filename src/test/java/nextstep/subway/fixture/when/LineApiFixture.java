package nextstep.subway.fixture.when;

import static nextstep.subway.fixture.given.LineRequestFixture.노선등록요청_데이터_생성;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public abstract class LineApiFixture {

    public static ExtractableResponse<Response> 지하철역_노선_등록_요청(String name, String color, long upStationId, long downStationId, int distance) {

        Map<String, Object> params = 노선등록요청_데이터_생성(name, color, upStationId, downStationId, distance);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static long 지하철역_노선_등록_요청_후_id_추출(String name, String color, long upStationId, long downStationId, int distance) {

        Map<String, Object> params = 노선등록요청_데이터_생성(name, color, upStationId, downStationId, distance);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract()
                .jsonPath()
                .getLong("id");
    }

    public static ExtractableResponse<Response> 지하철역_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_노선_단건_조회(long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_노선_수정(long id, Map<String, Object> params) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put("lines/{id}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_노선_삭제(long id) {
        return RestAssured
                .given().log().all()
                .when().delete("lines/{id}", id)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }
}
