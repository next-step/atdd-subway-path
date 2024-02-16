package nextstep.subway.steps;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.IntStream;

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

    public static ExtractableResponse<Response> 지하철_구간_추가요청_상태코드_검증_포함(Long upStationId, SectionRequest request, HttpStatus httpStatus) {
        return given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(String.format("/lines/%d/sections", upStationId))
                .then()
                .statusCode(httpStatus.value())
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제요청_상태코드_검증_포함(
            Long downStationIdToDelete, Long stationLineId, HttpStatus status) {
        return given().log().all()
                .when()
                .param("stationId", downStationIdToDelete)
                .delete(String.format("/lines/%d/sections", stationLineId))
                .then().statusCode(status.value()).extract();
    }

    public static void 지하철_구간_목록_추가요청_상태코드_검증_포함(Long 노선_번호, List<SectionRequest> 구간_요청_목록) {
        구간_요청_목록.forEach(구간_요청 -> 지하철_구간_추가요청_상태코드_검증_포함(노선_번호, 구간_요청, HttpStatus.CREATED));
    }

    public static JsonPath 지하철_구간_목록_조회_상태코드_검증_포함(
            Long stationLineId, HttpStatus httpStatus) {
        return given()
                .when()
                .get(String.format("/lines/%d/sections", stationLineId))
                .then().log().all()
                .statusCode(httpStatus.value())
                .extract().jsonPath();
    }

    public static List<Long> convertToStationIdsFromSection(JsonPath jsonPath) {
        Set<Long> allStationIds = new LinkedHashSet<>();

        List<Long> upStationIds = jsonPath.getList("upStation.id", Long.class);
        List<Long> downStationIds = jsonPath.getList("downStation.id", Long.class);

        IntStream.range(0, upStationIds.size())
                .forEach(index -> {
                    allStationIds.add(upStationIds.get(index));
                    allStationIds.add(downStationIds.get(index));
                });

        return new ArrayList<>(allStationIds);
    }
}
