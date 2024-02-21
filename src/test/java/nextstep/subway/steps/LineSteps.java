package nextstep.subway.steps;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static nextstep.subway.steps.StationSteps.*;
import static nextstep.subway.utils.HttpResponseUtils.getCreatedLocationId;
import static org.assertj.core.api.Assertions.assertThat;

public class LineSteps {

    public static JsonPath 모든_지하철_노선_조회_요청() {
        return given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }

    public static JsonPath 지하철_노선_조회_요청(Long stationLineId) {
        return given().log().all()
                .when()
                .get("/lines/" + stationLineId)
                .then().log().all()
                .extract().jsonPath();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청_검증_포함(LineRequest request) {
        return given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static void 지하철_노선_수정_요청(LineRequest request, ExtractableResponse<Response> 응답) {
        given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + getCreatedLocationId(응답))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }

    public static void 지하철_노선_삭제_요청(ExtractableResponse<Response> 응답) {
        given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + getCreatedLocationId(응답))
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    public static Long 지하철_노선_생성(LineRequest request) {
        return getCreatedLocationId(지하철_노선_생성_요청_검증_포함(request));
    }

    public static void 모든_지하철_노선_조회_검증(LineRequest... 노선_목록) {
        assertThat(convertLineResponses(모든_지하철_노선_조회_요청())).usingRecursiveComparison()
                .ignoringFields("id", "stations")
                .isEqualTo(List.of(노선_목록));
    }

    public static void 특정_지하철_노선_조회_검증(ExtractableResponse<Response> 응답, LineRequest 노선) {
        assertThat(convertLineResponse(지하철_노선_조회_요청(getCreatedLocationId(응답)))).usingRecursiveComparison()
                .ignoringFields("id", "stations")
                .isEqualTo(노선);
    }

    public static void 특정_지하철_노선_조회_구간_순서_검증(ExtractableResponse<Response> 응답, Long... 역_번호) {
        assertThat(convertStationIds(지하철_노선_조회_요청(getCreatedLocationId(응답))))
                .containsExactly(역_번호);
    }

    public static List<LineResponse> convertLineResponses(JsonPath jsonPath) {
        List<Long> ids = jsonPath.getList(ID_KEY, Long.class);
        List<String> names = jsonPath.getList(NAME_KEY, String.class);
        List<String> colors = jsonPath.getList(COLOR_KEY, String.class);

        return IntStream.range(0, names.size())
                .mapToObj(i -> new LineResponse(
                        ids.get(i),
                        names.get(i),
                        colors.get(i)
                ))
                .collect(Collectors.toList());
    }

    public static LineResponse convertLineResponse(JsonPath jsonPath) {
        return new LineResponse(
                jsonPath.getLong(ID_KEY),
                jsonPath.get(NAME_KEY).toString(),
                jsonPath.get(COLOR_KEY).toString()
        );
    }

}
