package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.StationTestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTestUtils {

    public static final Map<String, String> 신분당선_생성_요청 = new HashMap<>();

    public static final Map<String, String> 이호선_생성_요청 = new HashMap<>();

    static {
        신분당선_생성_요청.putAll(
                Map.of(
                        "name", "신분당선",
                        "color", "bg-red-600",
                        "upStationId", "",
                        "downStationId", "",
                        "distance", "10"
                )
        );

        이호선_생성_요청.putAll(
                Map.of(
                        "name", "이호선",
                        "color", "bg-green-600",
                        "upStationId", "",
                        "downStationId", "",
                        "distance", "20"
                )
        );
    }

    private LineTestUtils() {}

    public static String 지하철_노선_생성(Map<String, String> 노선_생성_요청_정보, String 상행역_URL, String 하행역_URL) {
        노선_생성_요청_정보.put("upStationId", String.valueOf(StationTestUtils.지하철_아이디_획득(상행역_URL)));
        노선_생성_요청_정보.put("downStationId", String.valueOf(StationTestUtils.지하철_아이디_획득(하행역_URL)));

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(노선_생성_요청_정보)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response.header("Location");
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .accept(ContentType.JSON)
                .get("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response;
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(String lineUrl) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .accept(ContentType.JSON)
                .get(lineUrl)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response;
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String lineUrl, Map<String, String> 노선_수정_요청_정보) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(노선_수정_요청_정보)
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put(lineUrl)
                .then().log().all()
                .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response;
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제(String lineUrl) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .delete(lineUrl)
                .then().log().all()
                .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        return response;
    }
}
