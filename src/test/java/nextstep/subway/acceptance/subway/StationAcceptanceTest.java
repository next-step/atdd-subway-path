package nextstep.subway.acceptance.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {
    private static final String TEST_STATION_NAME_1 = "을지로입구역";
    private static final String TEST_STATION_NAME_2 = "종각역";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStationTest() {
        // when
        ExtractableResponse<Response> response = createStation(TEST_STATION_NAME_1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(getStations().jsonPath().getList("name")).containsAnyOf(TEST_STATION_NAME_1);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStationsTest() {
        // given
        List<String> stations = List.of(TEST_STATION_NAME_1, TEST_STATION_NAME_2);
        stations.forEach(this::createStation);

        // when
        ExtractableResponse<Response> response = getStations();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).containsAll(stations);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역 제거")
    @Test
    void deleteStationTest() {
        // given
        String stationName = TEST_STATION_NAME_1;
        Long id = createStation(stationName).jsonPath().getLong("id");

        // when
        RestAssured.given()
                .when()
                    .delete("/stations/{id}", 1)
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

        // then
        assertThat(getStations().jsonPath().getList("id")).doesNotContain(id);
    }

    private ExtractableResponse<Response> createStation(String name) {
        return RestAssured.given()
                    .body(Map.of("name", name))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post("/stations")
                .then()
                    .extract();
    }

    private ExtractableResponse<Response> getStations() {
        return RestAssured.given()
                .when()
                    .get("/stations")
                .then()
                    .extract();
    }
}
