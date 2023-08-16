package subway.acceptance.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;

public class SubwayClient {

    public static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest param) {
        return RestAssured.given().log().all()
            .body(param)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 노선_생성_요청(LineRequest param) {
        return RestAssured.given().log().all()
            .body(param)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 노선_조회_요청(Long lineId) {
        return RestAssured.given().log().all()
            .when().get("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 노선_조회_요청() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 노선_수정_요청(Long lineId, LineRequest param) {
        return RestAssured.given().log().all()
            .body(param)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 노선_삭제_요청(Long lineId) {
        return RestAssured.given().log().all()
            .when().delete("/lines/" + lineId)
            .then().log().all()
            .extract();
    }


    public static ExtractableResponse<Response> 구간_생성_요청(Long lineId, SectionRequest param) {
        return RestAssured.given().log().all()
            .body(param)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/" + lineId + "/sections")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 구간_삭제_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
            .when().delete("/lines/" + lineId + "/sections?stationId=" + stationId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 경로_조회_요청(Long source, Long target) {
        return RestAssured.given().log().all()
            .when().get("/paths?source=" + source + "&target=" + target)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 경로_조회_요청() {
        return RestAssured.given().log().all()
            .when().get("/paths")
            .then().log().all()
            .extract();
    }

}
