package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.station.StationTestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.station.StationTestUtils.지하철_아이디_획득;

public class LineTestUtils {

    public static final Map<String, String> 신분당선_생성_요청 = new HashMap<>();
    public static final Map<String, String> 이호선_생성_요청 = new HashMap<>();
    public static final Map<String, String> 삼호선_생성_요청 = new HashMap<>();

    static {
        신분당선_생성_요청.putAll(
                Map.of(
                        "name", "신분당선",
                        "color", "bg-red-600",
                        "upStationId", "",
                        "downStationId", ""
                )
        );

        이호선_생성_요청.putAll(
                Map.of(
                        "name", "이호선",
                        "color", "bg-green-600",
                        "upStationId", "",
                        "downStationId", ""
                )
        );

        삼호선_생성_요청.putAll(
                Map.of(
                        "name", "삼호선",
                        "color", "bg-orange-600",
                        "upStationId", "",
                        "downStationId", ""
                )
        );
    }

    private LineTestUtils() {}

    public static String 지하철_노선_생성(Map<String, String> 노선_생성_요청_정보, String 상행역_URL, String 하행역_URL, int distance) {
        노선_생성_요청_정보.put("upStationId", String.valueOf(StationTestUtils.지하철_아이디_획득(상행역_URL)));
        노선_생성_요청_정보.put("downStationId", String.valueOf(StationTestUtils.지하철_아이디_획득(하행역_URL)));
        노선_생성_요청_정보.put("distance", String.valueOf(distance));

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(노선_생성_요청_정보)
                .when()
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
        return response.header("Location");
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .accept(ContentType.JSON)
                .get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(String lineUrl) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .accept(ContentType.JSON)
                .get(lineUrl)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
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
                .statusCode(HttpStatus.OK.value())
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제(String lineUrl) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .delete(lineUrl)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
        return response;
    }

    public static String 지하철_구간_등록(String 노선_url, String 새구간_상행역_url, String 새구간_하행역_url, int distance) {

        Map<String, String> 구간_등록_요청 = Map.of(
                "upStationId", String.valueOf(지하철_아이디_획득(새구간_상행역_url)),
                "downStationId", String.valueOf(지하철_아이디_획득(새구간_하행역_url)),
                "distance", String.valueOf(distance)
        );

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(구간_등록_요청)
                .when()
                .post(노선_url + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
        return response.header("Location");
    }

    public static void 지하철_구간_등록_실패(String 노선_url, String 새구간_상행역_url, String 새구간_하행역_url, int distance) {

        Map<String, String> 구간_등록_요청 = Map.of(
                "upStationId", String.valueOf(지하철_아이디_획득(새구간_상행역_url)),
                "downStationId", String.valueOf(지하철_아이디_획득(새구간_하행역_url)),
                "distance", String.valueOf(distance)
        );

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(구간_등록_요청)
                .when()
                .post(노선_url + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract();
    }

    public static void 지하철_구간_삭제(String 구간_url) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .delete(구간_url)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    public static void 지하철_구간_삭제_실패(String 구간_url) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .delete(구간_url)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract();
    }
}
