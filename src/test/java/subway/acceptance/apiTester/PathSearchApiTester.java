package subway.acceptance.apiTester;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.springframework.http.MediaType;
import subway.acceptance.utils.ApiTester;
import subway.acceptance.utils.TestParam;
import subway.application.response.PathResponse;
import subway.application.response.StationResponse;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@ApiTester
public class PathSearchApiTester {
    private final String URL = "/paths";

    private ExtractableResponse<Response> request(Long source, Long target) {

        TestParam param = TestParam.builder()
                .add("source", source)
                .add("target", target);

        return RestAssured
                .given().log().all()
                .queryParams(param.build())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(URL)
                .then().log().all()
                .extract();
    }


    public ExtractableResponse<Response> 경로_조회_요청(StationResponse startStation, StationResponse endStation) {
        return request(startStation.getId(), endStation.getId());
    }

    public void 경로_조회_응답됨(ExtractableResponse<Response> response, int distance, StationResponse... stations) {
        int reponseLength = stations.length;
        PathResponse pathResponse = response.body().as(PathResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(200),
                () -> assertThat(pathResponse)
                        .extracting("stations").asList()
                        .hasSize(reponseLength)
                        .extracting("id", "name")
                        .containsExactly(
                                Arrays.stream(stations)
                                        .map(station -> tuple(station.getId(), station.getName()))
                                        .toArray(Tuple[]::new)),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(distance));
    }

    public void 경로_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(400);

    }
}
