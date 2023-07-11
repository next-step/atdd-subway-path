package nextstep.subway;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {
    private static final String CREATE_STATION_PATH = "/stations";
    private static final String GET_STATION_LIST_PATH = "/stations";
    private static final String DELETE_STATION_PATH = "/stations";

    private StationFixture stationFixture = new StationFixture();

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        stationFixture.지하철역_생성("강남역");

        // then
        List<String> stationNames = 지하철역_목록조회("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    private <T> List<T> 지하철역_목록조회(String pathName, Class<T> elementType) {
        return RestAssured.given().log().all()
                          .when().get(GET_STATION_LIST_PATH)
                          .then().log().all()
                          .extract().jsonPath().getList(pathName, elementType);
    }

    private ExtractableResponse<Response> 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                   .body(params)
                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                   .when().post(CREATE_STATION_PATH)
                   .then().log().all()
                   .extract();
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다")
    @Test
    void getStationList() {
        Set<String> givenStationNames = Set.of("삼전역", "종합운동장역");
        givenStationNames.forEach(this::지하철역_생성);

        // when
        // 지하철역 목록 조회
        List<String> stationNames = 지하철역_목록조회("name", String.class);

        // then
        // 2개의 지하철 역을 응답으로 받는다
        assertThat(stationNames).containsAnyOf(givenStationNames.toArray(new String[0]));
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("생성된 지하철역을 제거한다")
    @Test
    void deleteStations() {
        // given
        String stationName = "강남역";
        ExtractableResponse<Response> createResponse = 지하철역_생성(stationName);
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Long createdStationId = extractCreateStationId(createResponse);

        // when
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                                                                  .when().delete(DELETE_STATION_PATH + "/" + createdStationId)
                                                                  .then().log().all().extract();

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        assertThat(지하철역_목록조회("id", Long.class)).doesNotContain(createdStationId);

    }

    private Long extractCreateStationId(ExtractableResponse<Response> response) {
        return Long.valueOf(response.header(HttpHeaders.LOCATION).split("/")[2]);
    }

}