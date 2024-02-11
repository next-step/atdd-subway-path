package nextstep.subway.steps;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

public class StationLineSteps {

    /**
     * 지하철 노선 목록을 조회하고 jsonPath 반환
     *
     * @return 지하철 노선 목록을 나타내는 jsonPath 반환
     */
    public static JsonPath 모든_지하철_노선_조회_요청() {
        return given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }

    /**
     * 주어진 지하철 노선 ID에 해당하는 지하철 노선 정보 반환
     *
     * @param stationLineId 지하철 노선 ID
     * @return 지하철 노선 정보를 나타내는 jsonPath 반환
     */
    public static JsonPath 지하철_노선_조회_요청(Long stationLineId) {
        return given().log().all()
                .when()
                .get("/lines/" + stationLineId)
                .then().log().all()
                .extract().jsonPath();
    }

    /**
     * 지하철 노선 생성 요청 후 Response 객체 반환
     *
     * @param request 지하철 요청 정보를 담은 객체
     * @return REST Assured 기반으로 생성된 Response 객체
     */
    public static ExtractableResponse<Response> 지하철_노선_생성_요청_검증_포함(LineRequest request) {
        return given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    /**
     * 지하철 노선 수정 요청 후 Response 객체 반환
     *
     * @param request       지하철 수정 정보를 담은 객체
     * @param stationLineId 수정할 지하철 노선 ID
     */
    public static void 지하철_노선_수정_요청(LineRequest request, Long stationLineId) {
        given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + stationLineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }

    /**
     * 지하철 노선 삭제 요청
     *
     * @param stationLineId 삭제할 지하철 노선 ID
     */
    public static void 지하철_노선_삭제_요청(Long stationLineId) {
        given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + stationLineId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
