package nextstep.subway.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;

public class SectionSteps {

    private static ExtractableResponse<Response> 지하철_구간_추가요청(Long lineId, SectionRequest request, HttpStatus httpStatus) {
        return given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(String.format("/lines/%d/sections", lineId))
                .then()
                .statusCode(httpStatus.value())
                .extract();
    }

    public static ExtractableResponse<Response> 성공하는_지하철_구간_추가요청(Long upStationId, SectionRequest request) {
        return 지하철_구간_추가요청(upStationId, request, HttpStatus.CREATED);
    }

    public static ExtractableResponse<Response> 실패하는_지하철_구간_추가요청(Long upStationId, SectionRequest request) {
        return 지하철_구간_추가요청(upStationId, request, HttpStatus.BAD_REQUEST);
    }

    public static void 지하철_구간_목록_추가요청_상태코드_검증_포함(Long 노선_번호, List<SectionRequest> 구간_요청_목록) {
        구간_요청_목록.forEach(구간_요청 -> 성공하는_지하철_구간_추가요청(노선_번호, 구간_요청));
    }

    private static ExtractableResponse<Response> 지하철_구간_삭제요청(
            Long downStationIdToDelete, Long stationLineId, HttpStatus httpStatus) {
        return given().log().all()
                .when()
                .param("stationId", downStationIdToDelete)
                .delete(String.format("/lines/%d/sections", stationLineId))
                .then()
                .statusCode(httpStatus.value())
                .extract();
    }

    public static ExtractableResponse<Response> 성공하는_지하철_구간_삭제요청(
            Long downStationIdToDelete, Long stationLineId) {
        return 지하철_구간_삭제요청(downStationIdToDelete, stationLineId, HttpStatus.NO_CONTENT);
    }

    public static ExtractableResponse<Response> 실패하는_지하철_구간_삭제요청(
            Long downStationIdToDelete, Long stationLineId) {
        return 지하철_구간_삭제요청(downStationIdToDelete, stationLineId, HttpStatus.BAD_REQUEST);
    }
}
