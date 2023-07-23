package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.stream.Collectors;

public class StationTestUtils {

    // 2호선
    public static final Map<String, String> 강남역_정보 = Map.of("name", "강남역");
    public static final Map<String, String> 역삼역_정보 = Map.of("name", "역삼역");
    public static final Map<String, String> 삼성역_정보 = Map.of("name", "삼성역");

    // 신분당선
    public static final Map<String, String> 판교역_정보 = Map.of("name", "판교역");
    public static final Map<String, String> 양재역_정보 = Map.of("name", "양재역");

    // 3호선
    public static final Map<String, String> 교대역_정보 = Map.of("name", "교대역");
    public static final Map<String, String> 남부터미널역_정보 = Map.of("name", "남부터미널역");


    // 기타
    public static final Map<String, String> 익명역_정보 = Map.of("name","익명역");

    public static Map<String, String> 역_저장_정보(Map<String, String> 역_정보, Long id) {
        Map<String, String> map = 역_정보.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        map.put("id", String.valueOf(id));
        return map;
    }

    public static Long 지하철_아이디_획득(String url) {
        return Long.parseLong(url.substring(url.lastIndexOf('/') + 1));
    }

    public static String 지하철_URL_생성(Long id) {
        return String.format("/stations/%s", id);
    }

    private StationTestUtils() {}

    public static void 지하철역_삭제(String stationUrl) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(stationUrl)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    public static String 지하철역_생성(Map<String, String> 지하철_정보) {

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(지하철_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
        return response.header("Location");
    }

    public static ExtractableResponse<Response> 지하철역_조회() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        return response;
    }
}
