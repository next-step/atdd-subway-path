package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.StationApiRequester.createStation;
import static nextstep.subway.acceptance.StationApiRequester.getStations;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@AcceptanceTest
public class StationAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역 이름이 주어지면, 지하철역을 생성한다")
    @Test
    void createStationTest() {
        // given
        final String stationName = "강남역";

        // when
        ExtractableResponse<Response> response = createStation(stationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> getStationsResponse = getStations();
        assertThat(getStationsResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> stationNames = getStationNames(getStationsResponse);

        assertThat(stationNames).containsAnyOf(stationName);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("여러 개의 지하철역 이름이 주어지면, 여러 개의 지하철역이 모두 생성되고, 모두 조회할 수 있다")
    @Test
    public void createStations() {
        // given
        List<String> stationNames = List.of("역삼역", "선릉역");
        List<ExtractableResponse<Response>> createResponses = stationNames.stream()
            .map(StationApiRequester::createStation).collect(Collectors.toList());

        createResponses.forEach(response ->
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        );

        // when
        ExtractableResponse<Response> response = getStations();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getStationNames(response))
            .allMatch(stationNames::contains);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역 생성 후 해당 지하철역 삭제요청을 보내면 성공적으로 삭제된다")
    @Test
    void createAndDeleteStation() {
        // given
        final String stationName = "삼성역";
        ExtractableResponse<Response> createResponse = createStation(stationName);

        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse.body().jsonPath().getString("name")).isEqualTo(stationName);

        long createdStationId = createResponse.body()
            .jsonPath()
            .getLong("id");

        assertThat(createdStationId).isNotNull();

        // when
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
            .pathParam("stationId", createdStationId)
            .when().delete("/stations/{stationId}")
            .then().log().all()
            .extract();

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        ExtractableResponse<Response> getStationsResponse = getStations();
        assertThat(getStationsResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getStationNames(getStationsResponse))
            .noneMatch(stationNames -> stationNames.equals(stationName));
    }

    /** 주어진 응답 객체에서 지하철역명을 값만 뽑아 반환합니다 */
    private List<String> getStationNames(
        final ExtractableResponse<Response> response
    ) {
        return response.jsonPath().getList("name", String.class);
    }
}
