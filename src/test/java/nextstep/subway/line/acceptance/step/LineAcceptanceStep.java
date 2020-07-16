package nextstep.subway.line.acceptance.step;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;

public class LineAcceptanceStep {
    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color) {
        return 지하철_노선_생성_요청(name, color);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("startTime", LocalTime.of(05, 30).format(DateTimeFormatter.ISO_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_TIME));
        params.put("intervalTime", "5");

        return RestAssured.given().log().all().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            body(params).
            when().
            post("/lines").
            then().
            log().all().
            extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all().
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            get("/lines").
            then().
            log().all().
            extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured.given().log().all().
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            get(uri).
            then().
            log().all().
            extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long lineId) {
        return RestAssured.given().log().all().
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            get("/lines/{lineId}", lineId).
            then().
            log().all().
            extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> response, String name,
        String color) {
        String uri = response.header("Location");

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("startTime", LocalTime.of(05, 30).format(DateTimeFormatter.ISO_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_TIME));
        params.put("intervalTime", "5");

        return RestAssured.given().log().all().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            body(params).
            when().
            put(uri).
            then().
            log().all().
            extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured.given().log().all().
            when().
            delete(uri).
            then().
            log().all().
            extract();
    }

    public static void 지하철_노선_생성됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_응답됨(ExtractableResponse<Response> response,
        ExtractableResponse<Response> createdResponse) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(LineResponse.class)).isNotNull();
    }

    public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response,
        List<ExtractableResponse<Response>> createdResponses) {
        List<Long> expectedLineIds = createdResponses.stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
