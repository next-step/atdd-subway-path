package nextstep.subway.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;

public class StationSectionSteps {

    public static ExtractableResponse<Response> 지하철_구간_추가요청_검증_생략(Long upStationId, SectionRequest request) {
        return given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(String.format("/lines/%d/sections", upStationId))
                .then().extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_추가요청_상태코드_검증_포함(Long upStationId, SectionRequest request) {
        return given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(String.format("/lines/%d/sections", upStationId))
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제요청_검증_생략(
            Long downStationIdToDelete, Long stationLineId) {
        return given().log().all()
                .when()
                .param("stationId", downStationIdToDelete)
                .delete(String.format("/lines/%d/sections", stationLineId))
                .then().extract();
    }

    public static void 지하철_구간_목록_추가요청_상태코드_검증_포함(Long 노선_번호, List<SectionRequest> 구간_요청_목록) {
        구간_요청_목록.forEach(구간_요청 -> 지하철_구간_추가요청_상태코드_검증_포함(노선_번호, 구간_요청));
    }
}
