package nextstep.subway.acceptance.path;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.ShortestPathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.station.StationTestUtils.지하철_아이디_획득;
import static org.assertj.core.api.Assertions.assertThat;

public class PathTestUtils {

    private PathTestUtils() {}

    public static ShortestPathResponse 지하철_최단_경로_조회(String 출발역_URL, String 도착역_URL) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(ContentType.JSON)
                .queryParam("source", 지하철_아이디_획득(출발역_URL))
                .queryParam("target", 지하철_아이디_획득(도착역_URL))
                .when()
                .get("/paths")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        ShortestPathResponse shortestPathResponse = response.body().as(ShortestPathResponse.class);
        return shortestPathResponse;
    }

    public static void 지하철_최단_경로_조회_실패(String 출발역_URL, String 도착역_URL) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(ContentType.JSON)
                .queryParam("source", 지하철_아이디_획득(출발역_URL))
                .queryParam("target", 지하철_아이디_획득(도착역_URL))
                .when()
                .get("/paths")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract();
    }

    public static void 최단_경로_길이는_다음과_같다(ShortestPathResponse 경로_조회_응답, long distance) {
        assertThat(경로_조회_응답.getDistance()).isEqualTo(distance);
    }

    public static void 경로_조회_결과는_다음과_같다(ShortestPathResponse 경로_조회_응답, String... 지하철역_URLs) {
        List<Long> idList = Arrays.stream(지하철역_URLs)
                .map(url -> 지하철_아이디_획득(url))
                .collect(Collectors.toList());

        Arrays.stream(지하철역_URLs).collect(Collectors.toList());
        assertThat(경로_조회_응답.getStations().stream().map(StationResponse::getId))
                .containsExactlyElementsOf(idList);
    }
}
