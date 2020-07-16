package nextstep.subway.station.acceptance.step;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;

public class StationAcceptanceStep {
    public static ExtractableResponse<Response> 지하철역_등록되어_있음(String name) {
        return 지하철역_생성_요청(name);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/stations").
            then().
            log().all().
            extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return RestAssured.given().log().all().
            when().
            get("/stations").
            then().
            log().all().
            extract();
    }

    public static ExtractableResponse<Response> 지하철역_제거_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured.given().log().all().
            when().
            delete(uri).
            then().
            log().all().
            extract();
    }

    public static void 지하철역_생성됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철역_목록_포함됨(ExtractableResponse<Response> response,
        List<ExtractableResponse<Response>> createdResponses) {
        List<Long> expectedLineIds = createdResponses.stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }
}
