package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StationAcceptanceTest {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        지하철역을_생성한다("강남역");

        // then
        List<String> stationNames = 지하철역_목록을_조회한다();
        생성한_역을_목록에서_찾을_수_있다(stationNames, "강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void showStations() {
        // given
        지하철역을_생성한다("강남역");
        지하철역을_생성한다("건대입구역");

        // when
        List<String> stationNames = 지하철역_목록을_조회한다();

        // then
        생성한_갯수의_지하철역_목록을_응답한다(stationNames, 2);
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
        ExtractableResponse<Response> createdResponse = 지하철역을_생성한다("건대입구역");

        // when
        지하철역을_삭제한다(createdResponse.header("Location"));

        // then
        List<String> stationNames = 지하철역_목록을_조회한다();
        삭제한_역은_조회되지_않는다(stationNames, "건대입구역");
    }

    private static void 생성한_역을_목록에서_찾을_수_있다(List<String> stationNames, String createdName) {
        assertThat(stationNames).containsAnyOf(createdName);
    }

    private static void 지하철역을_삭제한다(String createdResourceUrl) {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(createdResourceUrl)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private static void 삭제한_역은_조회되지_않는다(List<String> stationNames, String deletedName) {
        assertThat(deletedName).isNotIn(stationNames);
    }

    private static void 생성한_갯수의_지하철역_목록을_응답한다(List<String> stationNames, int createdCount) {
        assertThat(stationNames.size()).isEqualTo(createdCount);
    }

    private static List<String> 지하철역_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("name", String.class);
    }

    private static ExtractableResponse<Response> 지하철역을_생성한다(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }
}
