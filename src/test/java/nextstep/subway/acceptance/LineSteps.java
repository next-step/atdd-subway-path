package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.fixture.LineFixture;
import nextstep.subway.fixture.StationFixture;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;

import static nextstep.subway.acceptance.StationSteps.역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class LineSteps {

    private static final String PATH = "/lines";

    public static ExtractableResponse<Response> 노선_생성_요청(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(ContentType.JSON)
                .when()
                .post(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_조회_요청(String uri) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when()
                .get(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_수정_요청(String uri, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_삭제_요청(String uri) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 신분당선_생성_완료() {
        var station1 = StationFixture.신논현역;
        var station2 = StationFixture.강남역;
        역_생성_요청(station1);
        역_생성_요청(station2);

        var params = LineFixture.신분당선;
        return LineSteps.노선_생성_요청(params);
    }


    public static void 노선_생성_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 노선_목록_조회_완료(ExtractableResponse<Response> response, Map<String, Object>... paramsArgs) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        var lineNames = response.jsonPath().getList("name");
        assertThat(lineNames).contains(Arrays.stream(paramsArgs).map(m -> m.get("name")).toArray());
    }

    public static void 노선_조회_완료(
            ExtractableResponse<Response> response,
            Map<String, Object> params,
            Map<String, String>... stationFixtures
    ) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        var lineName = response.jsonPath().getString("name");
        var stations = response.jsonPath().getList("stations.name");
        assertThat(lineName).isEqualTo(params.get("name"));
        assertThat(stations).containsExactlyInAnyOrder(Arrays.stream(stationFixtures).map(m -> m.get("name")).toArray());
    }

    public static void 노선_수정_완료(ExtractableResponse<Response> response, Map<String, Object> modifyParams) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        var lineName = response.jsonPath().getString("name");
        assertThat(lineName).isEqualTo(modifyParams.get("name"));
    }

    public static void 노선_삭제_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 중복된_노선_생성_예외(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
