package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.steps.StationSteps.createStation;
import static nextstep.subway.acceptance.steps.StationSteps.getStations;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역 생성")
    @Test
    void createStationTest() {
        // when
        ExtractableResponse<Response> response = createStation("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getStations().jsonPath().getList("name",String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }



    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록 조회")
    @Test
    void getStationList() {
        //Given
        createStation("강남역");
        createStation("역삼역");

        //When
        ExtractableResponse<Response> response = getStations();
        List<String> station = response.jsonPath().getList("name", String.class);

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(station).contains("강남역","역삼역");

    }



    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    // TODO: 지하철역 제거 인수 테스트 메서드 생성
    @DisplayName("지하철역 제거")
    @Test
    public void deleteStation() {
        //Given
        createStation("강남역");

        Long stationId = RestAssured.given().log().all()
                .when().get("stations")
                .then().log().all()
                .extract().jsonPath().getList("id", Long.class).get(0);


        //When
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                .when().delete("stations/" + stationId)
                .then().log().all()
                .extract();

        //Then
        ExtractableResponse<Response> response = getStations();
        List<String> stations = response.jsonPath().getList("name", String.class);

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(stations).isEmpty();
    }
}