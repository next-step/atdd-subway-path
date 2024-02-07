package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    private final String routePrefix = "/stations";

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
                        .when().post(routePrefix)
                        .then().log().all()
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(getStationNameList()).containsExactly("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStationList() {
        // given
        setupStationByName("선릉역");
        setupStationByName("역삼역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get(routePrefix)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).containsExactly("선릉역", "역삼역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        // given
        Long setupStationId = setupStationByName("역삼역");

        // when
        ExtractableResponse<Response> deleteStationResponse =
                RestAssured.given().log().all()
                        .pathParam("id", setupStationId)
                        .when().delete(routePrefix + "/{id}")
                        .then().log().all()
                        .extract();

        // then
        assertThat(deleteStationResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(getStationNameList()).doesNotContain("역삼역");
    }

    /**
     * 지하철역을 생성하고 생성된 id 값을 반환한다
     */
    private Long setupStationByName(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(routePrefix)
                .then().log().all()
                .extract().jsonPath().getLong("id");
    }

    private List<String> getStationNameList() {
        return RestAssured.given().log().all()
                .when().get(routePrefix)
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

    }
}
