package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import nextstep.subway.dto.station.StationRequest;
import nextstep.subway.dto.station.StationResponse;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        ExtractableResponse<Response> response = requestCreateStation("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames =
                RestAssured.given().log().all()
                        .when().get("/stations")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        // given
        List<String> requestNames = asList("강남역", "역삼역", "선릉역");

        for (String requestName : requestNames) {
            ExtractableResponse<Response> response = requestCreateStation(requestName);

            assertThat(response.as(StationResponse.class).getName()).isEqualTo(requestName);
        }

        // when
        List<StationResponse> responses = RestAssured
                .given().log().all()
                .when()
                    .get("/stations")
                .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                .extract()
                    .jsonPath()
                    .getList(".", StationResponse.class);

        List<String> responseName = responses.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(responseName).containsAll(requestNames);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void deleteStation() {
        // given
        String requestName = "강남역";

        StationResponse createResponse =
            requestCreateStation(requestName).as(StationResponse.class);

        assertThat(createResponse.getName()).isEqualTo(requestName);

        // when
        ExtractableResponse<Response> deleteResponse = RestAssured
                .given().log().all()
                    .pathParam("id", createResponse.getId())
                .when()
                    .delete("/stations/{id}")
                .then().log().all()
                .extract();

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> requestCreateStation(String name) {
        return RestAssured.given().log().all()
            .body(new StationRequest(name))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

}