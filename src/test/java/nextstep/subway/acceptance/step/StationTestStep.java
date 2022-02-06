package nextstep.subway.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationTestStep {

    public static Long 지하철역_생성_후_아이디_추출하기(String stationName) {
        ExtractableResponse<Response> response = 지하철역_생성하기(stationName);
        Integer stationIntegerId = response.jsonPath().get("id");
        return stationIntegerId.longValue();
    }

    public static ExtractableResponse<Response> 지하철역_생성하기(String stationName) {
        Map<String, String> body = new HashMap<>();
        body.put("name", stationName);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static void 지하철역_생성_시_성공_검증하기(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철역_생성_시_중복이름_실패_검증하기(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회하기() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static void 지하철역_목록_조회_성공_검증하기(ExtractableResponse<Response> response,
                                       String stationName1,
                                       String stationName2) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stationNames = response.jsonPath().getList("name");
        assertThat(stationNames).contains(stationName1, stationName2);
    }

    public static ExtractableResponse<Response> 지하철역_삭제하기(Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/stations/" + stationId)
                .then().log().all()
                .extract();
    }

    public static void 지하철역_삭제_성공_검증하기(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
