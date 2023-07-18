package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.station.StationTestStepDefinition.지하철_역_생성_요청;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import nextstep.subway.common.exception.BaseExceptionResponse;
import nextstep.subway.station.StationResponse;

public class LineTestStepDefinition {

    public static LineResponse 지하철_노선_생성_요청(String lineName, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest lineRequest = new LineRequest(lineName, color, upStationId, downStationId, distance);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when().post("/lines")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return response.as(LineResponse.class);
    }

    public static LineResponse 지하철_노선_생성_요청(String lineName, String color, String upStationName, String downStationName, int distance) {
        StationResponse upStationResponse = 지하철_역_생성_요청(upStationName);
        StationResponse downStationResponse = 지하철_역_생성_요청(downStationName);

        return 지하철_노선_생성_요청(lineName, color, upStationResponse.getId(), downStationResponse.getId(), distance);
    }

    public static LineResponse 지하철_노선_조회_요청(Long id) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/" + id)
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response.as(LineResponse.class);
    }

    public static BaseExceptionResponse 없는_지하철_노선_조회_요청(Long id) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/" + id)
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response.as(BaseExceptionResponse.class);
    }

    public static List<LineResponse> 지하철_노선_목록_조회_요청() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response.jsonPath().getList(".", LineResponse.class);
    }

    public static void 지하철_노선_수정_요청(Long id, String name, String color) {
        LineUpdateRequest request = new LineUpdateRequest(name, color);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when().put("/lines/" + id)
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_삭제_요청(Long id) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/" + id)
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
