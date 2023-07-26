package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("지하철역 관련 기능")
@Sql("/teardown.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {

    public static final String 강남역 = "강남역";
    public static final String 양재역 = "양재역";
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUpPort(){
        RestAssured.port = port;
    }
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 역_만들기(강남역);

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
    // TODO: 지하철역 목록 조회 인수 테스트 메서드 생성
    @DisplayName("지하철역을 생성하고 지하철 역을 조회한다.")
    @Test
    void createStationAndGetStation() {
        // Given
        역_만들기(강남역);
        역_만들기(양재역);

        // When
        ExtractableResponse<Response> responseStations = 지하철역_조회하기();

        // Then
        assertThat(responseStations.jsonPath().getList(".").stream().count()).isEqualTo(2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    // TODO: 지하철역 제거 인수 테스트 메서드 생성
    @DisplayName("지하철역을 생성하고 삭제하면 지하철 목록에 지하철역이 없다.")
    @Test
    void createStationAndDeleteStationAndGetStationList() {
        // Given
        ExtractableResponse<Response> responseStationGangnam = 역_만들기(강남역);
        String createdId = responseStationGangnam.jsonPath().getString("id");

        // When
        역_삭제하기(createdId);

        // Then
        ExtractableResponse<Response> responseStations = 지하철역_조회하기();
        assertThat(responseStations.jsonPath().getList("name")).doesNotContain(강남역);
    }
    protected static ExtractableResponse<Response> 역_만들기(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
        return response;
    }

    private static ExtractableResponse<Response> 역_삭제하기(String id) {
        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(String.format("/stations/%s",id))
                .then().log().all()
                .extract();
        return response;
    }
    private static ExtractableResponse<Response> 지하철역_조회하기() {

        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract();
        return response;
    }
}
