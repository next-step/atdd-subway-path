package nextstep.subway.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static nextstep.subway.utils.HttpResponseUtils.getCreatedLocationId;

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

    public static ExtractableResponse<Response> 성공하는_지하철_구간_추가요청(Long 노선_번호, SectionRequest request) {
        return 지하철_구간_추가요청(노선_번호, request, HttpStatus.CREATED);
    }

    public static ExtractableResponse<Response> 성공하는_지하철_구간_추가요청(ExtractableResponse<Response> 응답, SectionRequest request) {
        return 지하철_구간_추가요청(getCreatedLocationId(응답), request, HttpStatus.CREATED);
    }

    public static ExtractableResponse<Response> 실패하는_지하철_구간_추가요청(ExtractableResponse<Response> 응답, SectionRequest request) {
        return 지하철_구간_추가요청(getCreatedLocationId(응답), request, HttpStatus.BAD_REQUEST);
    }

    public static void 지하철_구간_목록_추가요청_상태코드_검증_포함(ExtractableResponse<Response> 응답, List<SectionRequest> 구간_요청_목록) {
        구간_요청_목록.forEach(구간_요청 -> 성공하는_지하철_구간_추가요청(응답, 구간_요청));
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
            Long downStationIdToDelete, ExtractableResponse<Response> 응답) {
        return 지하철_구간_삭제요청(downStationIdToDelete, getCreatedLocationId(응답), HttpStatus.NO_CONTENT);
    }

    public static ExtractableResponse<Response> 실패하는_지하철_구간_삭제요청(
            Long downStationIdToDelete, ExtractableResponse<Response> 응답) {
        return 지하철_구간_삭제요청(downStationIdToDelete, getCreatedLocationId(응답), HttpStatus.BAD_REQUEST);
    }
}
