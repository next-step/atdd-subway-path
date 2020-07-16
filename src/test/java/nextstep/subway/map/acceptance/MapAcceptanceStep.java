package nextstep.subway.map.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import nextstep.subway.map.dto.MapResponse;

public class MapAcceptanceStep {

    public static ExtractableResponse<Response> 지하철_노선도를_캐시로_요청한다() {
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/maps")
            .then()
            .log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선도를_캐시로_요청한다(String eTag) {
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .header("If-None-Match", eTag)
            .get("/maps")
            .then()
            .log().all()
            .extract();
    }

    public static void 지하철_노선도가_응답된다(ExtractableResponse<Response> response) {
        int statusCode = response.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선도에_eTag가_명시된다(ExtractableResponse<Response> response) {
        assertThat(response.header("eTag")).isNotNull();
    }

    public static void 지하철_노선도_응답시_캐시가_적용된다(ExtractableResponse<Response> response) {
        int statusCode = response.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NOT_MODIFIED.value());
    }

    public static void 지하철_노선도에_노선별로_지하철역_순서가_정렬된다(ExtractableResponse<Response> response, final Long lineId,
        final List<Long> stationIds) {
        LineResponses lines = response.as(MapResponse.class).getLineResponses();
        LineResponse line = lines.getLineResponseByLineId(lineId);
        assertThat(line.getStations())
            .extracting(it -> it.getStation().getId())
            .containsExactlyElementsOf(stationIds);
    }
}
