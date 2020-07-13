package nextstep.subway.map.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선도 인수 테스트")
public class MapAcceptanceStep {

    public static void 지하철_노선도에_노선별_지하철역_순서_정렬됨(ExtractableResponse<Response> response, Long lineId, List<Long> lineIdsInOrder) {
        LineResponse lineResponse = extractLineResponseById(response, lineId);
        assertThat(lineResponse.getStations())
                .extracting(LineStationResponse::getStation)
                .extracting(StationResponse::getId)
                .containsExactlyElementsOf(lineIdsInOrder);
    }

    public static void 지하철_노선도_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static ExtractableResponse<Response> 지하철_노선도_조회_요청() {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/maps")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선도_조회_요청_캐시_적용(String etag) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.IF_NONE_MATCH, etag)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/maps")
                .then()
                .log().all()
                .extract();
    }

    private static LineResponse extractLineResponseById(ExtractableResponse<Response> response, Long lineId) {
        return response.as(MapResponse.class).getLines().stream()
                .filter(line -> Objects.equals(line.getId(), lineId))
                .findAny()
                .get();
    }
}
