package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.StationRequest;
import nextstep.subway.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@Sql(scripts = "classpath:truncate-tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();

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
    @DisplayName("지하철 생성 및 지하철역 목록을 조회한다.")
    @Test
    void testShowSubwayStations() {
        //given
        StationRequest stationRequest1 = new StationRequest("종합운동장");
        StationRequest stationRequest2 = new StationRequest("잠실");

        ExtractableResponse<Response> firstResponse =
          RestAssured.given().log().all()
            .body(stationRequest1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();

        ExtractableResponse<Response> secondResponse = RestAssured.given().log().all()
          .body(stationRequest2)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .when().post("/stations")
          .then().log().all()
          .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
          .when().get("/stations")
          .then().log().all()
          .extract();

        //then
        // 2개의 지하철 역 생성을 응답 받는다.
        assertThat(firstResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(secondResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // 지하철 역 조회를 응답 받는다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철 역 조회 결과에 이름이 포함되어 있고 크기가 일치하는지 확인한다.
        List<String> stationNames = response.jsonPath().getList("name", String.class);
        assertThat(stationNames).contains("종합운동장", "잠실");
        assertThat(stationNames.size()).isEqualTo(2);

        //지하철 역 조회 결과의 아이디 순서를 확인하고 요청 순서에 따른 생성된 이름 결과가 일치하는지 확인한다.
        List<StationResponse> stations = response.jsonPath().getList("", StationResponse.class);
        assertThat(stations.get(0).getId()).isEqualTo(1L);
        assertThat(stations.get(0).getName()).isEqualTo("종합운동장");
        assertThat(stations.get(1).getId()).isEqualTo(2L);
        assertThat(stations.get(1).getName()).isEqualTo("잠실");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역 생성 후 지하철역을 삭제한다.")
    @Test
    public void testDeleteStation() {
        //given
        ExtractableResponse<Response> createResponse =
          RestAssured.given().log().all()
            .body(new StationRequest("종합운동장"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();


        //when
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .when().delete("/stations/1")
          .then().log().all()
          .extract();

        //then
        ExtractableResponse<Response> showResponse = RestAssured.given().log().all()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .when().get("/stations")
          .then().log().all()
          .extract();

        //요청 결과 응답 코드가 일치하는지 확인한다.
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(showResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철 역 조회 결과에 삭제이후 이름이 포함되어 있지 않고 크기가 0인지 확인한다.
        List<String> stationNames = showResponse.jsonPath().getList("name", String.class);
        assertThat(stationNames).doesNotContain("종합운동장");
        assertThat(stationNames).size().isEqualTo(0);
    }
}
