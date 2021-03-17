package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LineSteps {

    public static ExtractableResponse<Response> 지하철_노선_생성요청(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_요청에대한_응답_확인(ExtractableResponse<Response> response, LineResponse line) {
        LineResponse lineResponse = response.as(LineResponse.class);
        assertAll(
                () -> assertEquals(line.getName(), lineResponse.getName()),
                () -> assertEquals(line.getColor(), lineResponse.getColor())
        );
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then()
                .log().all().extract();
    }

    public static void 지하철_노선_목록_응답_확인(ExtractableResponse<Response> response, List<LineResponse> line) {
        List<LineResponse> lineResults = response.body().jsonPath().getList(".", LineResponse.class);

        List<List<String>> list =
                line.stream()
                        .map(LineSteps::responseToList)
                        .collect(Collectors.toList());

        assertThat(lineResults)
                .extracting(LineSteps::responseToList)
                .containsAll(list);
    }

    public static void 지하철_노선_응답_확인(int statusCode, HttpStatus status) {
        assertThat(statusCode).isEqualTo(status.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(long lineId) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(long lineId, LineRequest line) {
        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(line).
                when().
                put("/lines/{id}", lineId).
                then().
                log().all().
                extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(LineResponse line) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/{id}", line.getId())
                .then().log().all()
                .extract();
    }

    private static List<String> responseToList(LineResponse lineResponse) {
        return Arrays.asList(lineResponse.getName(), lineResponse.getColor());
    }
}
