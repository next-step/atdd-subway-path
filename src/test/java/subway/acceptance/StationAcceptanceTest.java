package subway.acceptance;

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

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {

    @LocalServerPort
    int port;;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    private final String 강남역 = "강남역";
    private final String 잠실역 = "잠실역";

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
        params.put("name", 강남역);

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
        assertThat(stationNames).containsAnyOf(강남역);
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
        Map<String, String> station1 = new HashMap<>() {{ put("name", "강남역"); }};
        Map<String, String> station2 = new HashMap<>() {{ put("name", "잠실역"); }};

        RestAssured.given().log().all()
            .body(station1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value());

        RestAssured.given().log().all()
            .body(station2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value());

        // when
        List<String> stationNames =
            RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

        // then
        assertThat(stationNames).containsExactly(강남역, 잠실역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> station = new HashMap<>() {{ put("name", 강남역); }};

        ExtractableResponse<Response> stationCreateResponse =
            RestAssured.given().log().all()
                .body(station)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();

        assertThat(stationCreateResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        RestAssured.given().log().all()
            .body(station)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(stationCreateResponse.header("Location"))
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());

        // then
        List<String> stationNames =
            RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

        assertThat(stationNames).doesNotContain(강남역);
    }

}