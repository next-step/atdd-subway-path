package nextstep.subway.acceptance.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import nextstep.subway.acceptance.station.StationTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionUtils {

    public static final Map<String, String> 구간_등록_요청 = new HashMap<>();

    static {
        구간_등록_요청.putAll(
                Map.of(
                        "downStationId", "",
                        "upStationId", "",
                        "distance", "10"
                )
        );
    }

    private SectionUtils() {}

    public static String 지하철_구간_등록(String 노선_url, Map<String, String> 구간_등록_요청, String 새구간_상행역_url, String 새구간_하행역_url) {

        구간_등록_요청.put("downStationId", String.valueOf(StationTestUtils.지하철_아이디_획득(새구간_하행역_url)));
        구간_등록_요청.put("upStationId", String.valueOf(StationTestUtils.지하철_아이디_획득(새구간_상행역_url)));

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(구간_등록_요청)
                .when()
                .post(노선_url + "/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return response.header("Location");
    }

    public static void 지하철_구간_등록_실패(String 노선_url, Map<String, String> 구간_등록_요청, String 새구간_상행역_url, String 새구간_하행역_url) {

        구간_등록_요청.put("downStationId", String.valueOf(StationTestUtils.지하철_아이디_획득(새구간_하행역_url)));
        구간_등록_요청.put("upStationId", String.valueOf(StationTestUtils.지하철_아이디_획득(새구간_상행역_url)));

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(구간_등록_요청)
                .when()
                .post(노선_url + "/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_구간_삭제(String 구간_url) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .delete(구간_url)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_구간_삭제_실패(String 구간_url) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .delete(구간_url)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
