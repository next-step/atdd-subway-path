package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static nextstep.subway.acceptance.station.StationTestUtils.지하철_아이디_획득;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionUtils {

    enum SectionDistance {
        MEDIUM,
        BIG
    }

    private SectionUtils() {}

    public static String 지하철_구간_등록(String 노선_url, String 새구간_상행역_url, String 새구간_하행역_url, SectionDistance distance) {

        Map<String, String> 구간_등록_요청 = Map.of(
                "upStationId", String.valueOf(지하철_아이디_획득(새구간_상행역_url)),
                "downStationId", String.valueOf(지하철_아이디_획득(새구간_하행역_url)),
                "distance", String.valueOf(distance.ordinal())
        );

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

    public static void 지하철_구간_등록_실패(String 노선_url, String 새구간_상행역_url, String 새구간_하행역_url, SectionDistance distance) {

        Map<String, String> 구간_등록_요청 = Map.of(
                "upStationId", String.valueOf(지하철_아이디_획득(새구간_상행역_url)),
                "downStationId", String.valueOf(지하철_아이디_획득(새구간_하행역_url)),
                "distance", String.valueOf(distance.ordinal())
        );

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
