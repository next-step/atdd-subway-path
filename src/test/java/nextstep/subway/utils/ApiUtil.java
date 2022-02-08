package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.test.utils.Lines;
import nextstep.subway.acceptance.test.utils.Paths;
import nextstep.subway.acceptance.test.utils.Stations;
import org.springframework.http.MediaType;

public class ApiUtil {
    public static ExtractableResponse<Response> 지하철_노선_생성_API(Lines.지하철_노선_생성_파람 params) {
        return RestAssured.given().log().all()
                .body(params.toMap())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_전체_리스트_조회_API() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_단건_조회_API(Long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_API(Long id, Lines.지하철_노선_수정_파람 updateParams) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .body(updateParams.toMap())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_API(Long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_등록_API(Long id, Lines.지하철_구간_생성_파람 params) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .body(params.toMap())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_삭제_API(Long id, Lines.지하철_구간_삭제_파람 params) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .params(params.toMap())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_생성_API(Stations.지하철역_생성_파람 params) {
        return RestAssured.given().log().all()
                .body(params.toMap())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_전체_리스트_조회_API() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제_API(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 최단거리_탐색_API(Paths.최단거리_탐색_파람 params) {
        return RestAssured.given().log().all()
                .params(params.toMap())
                .when()
                .get("/paths")
                .then().log().all()
                .extract();
    }

}
