package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.acceptance.StationAcceptanceTest.역_만들기;

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

@DisplayName("지하철노선 관련 기능")
@Sql("/teardown.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {

    public static final String 신분당선 = "신분당선";
    public static final String 분당선 = "분당선";
    public static final String 경인선 = "경인선";

    public static final String BG_YELLOW_600 = "BG_YELLOW_600";
    public static final String BG_RED_600 = "bg-red-600";
    public static final String BG_GREEN_600 = "BG_GREEN_600";

    public static final String 거리 = "10";
    public static final String 첫째지하철역1 = "첫째지하철역";
    public static final String 세번째지하철역1 = "세번째지하철역";
    public static final String 두번째지하철역1 = "두번째지하철역";
    @LocalServerPort
    int port;
    private ExtractableResponse<Response> 첫째지하철역;
    private ExtractableResponse<Response> 두번째지하철역;
    private ExtractableResponse<Response> 세번째지하철역;

    private String 첫째지하철역_아이디;
    private String 두번째지하철역_아이디;
    private String 세번째지하철역_아이디;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        // 공통 GIVEN
        세개의_역_생성();
    }

    public void 세개의_역_생성() {
        첫째지하철역 = 역_만들기(첫째지하철역1);
        두번째지하철역 = 역_만들기(두번째지하철역1);
        세번째지하철역 = 역_만들기(세번째지하철역1);

        첫째지하철역_아이디 = 첫째지하철역.jsonPath().getString("id");
        두번째지하철역_아이디 = 두번째지하철역.jsonPath().getString("id");
        세번째지하철역_아이디 = 세번째지하철역.jsonPath().getString("id");
    }

    /**
     * 지하철노선 생성
     * When 지하철노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성하고 생성한 노선을 찾을 수 있다.")
    @Test
    void createLine() {
        // When
        ExtractableResponse<Response> response = 노선_만들기(신분당선, BG_RED_600, 첫째지하철역_아이디, 두번째지하철역_아이디, "10");

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // Then
        List<String> lineNames = 모든_노선_조회();
        assertThat(lineNames).containsAnyOf("신분당선");
    }

    /**
     * 지하철노선 목록 조회
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철역을 2개 생성하고 목록에서 2개의 노선을 조회할 수 있다.")
    @Test
    void getLineList() {
        // Given
        노선_만들기(신분당선, BG_RED_600, 첫째지하철역_아이디, 두번째지하철역_아이디, 거리);
        노선_만들기(분당선, BG_GREEN_600, 첫째지하철역_아이디, 세번째지하철역_아이디, 거리);

        // When
        List<String> lineNames = 모든_노선_조회();

        // Then
        assertThat(lineNames).contains(신분당선, 분당선);
    }

    /**
     * 지하철노선 조회
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */

    @DisplayName("지하철 노선을 생성하고 생성한 지하철의 노선의 정보를 응답 받을 수 있다.")
    @Test
    void GetLineById() {

        // Given
        ExtractableResponse<Response> lineNewBundangResponse = 노선_만들기(신분당선, BG_RED_600, 첫째지하철역_아이디, 두번째지하철역_아이디, 거리);
        String createdId = lineNewBundangResponse.jsonPath().getString("id");

        // When
        ExtractableResponse<Response> response = 아이디_노선_조회(createdId);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo(신분당선);
        assertThat(response.jsonPath().getString("color")).isEqualTo(BG_RED_600);
        assertThat(response.jsonPath().getList("stations.name")).contains(첫째지하철역1, 두번째지하철역1);
    }

    /**
     * 지하철노선 수정
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 생성하고 해당 지하철 노선 정보는 수정된다.")
    @Test
    void EditLine() {
        // Given
        ExtractableResponse<Response> lineNewBundangResponse = 노선_만들기(신분당선, BG_RED_600, 첫째지하철역_아이디, 두번째지하철역_아이디, 거리);
        String createdId = lineNewBundangResponse.jsonPath().getString("id");

        // When
        ExtractableResponse<Response> response = 노선_수정(createdId, 경인선, BG_YELLOW_600);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // When
        ExtractableResponse<Response> lineKyungInResponse = 아이디_노선_조회(createdId);

        //Then
        assertThat(lineKyungInResponse.jsonPath().getString("name")).isEqualTo(경인선);
        assertThat(lineKyungInResponse.jsonPath().getString("color")).isEqualTo(BG_YELLOW_600);
    }

    /**
     * 지하철노선 삭제
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 생성하고 해당 지하철 노선 정보는 삭제된다.")
    @Test
    void deleteLine() {
        // Given
        ExtractableResponse<Response> lineNewBundangResponse = 노선_만들기(신분당선, BG_RED_600, 첫째지하철역_아이디, 두번째지하철역_아이디, 거리);
        String createdId = lineNewBundangResponse.jsonPath().getString("id");

        // When
        ExtractableResponse<Response> response = 노선_삭제(createdId);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // When
        List<String> lineNames = 모든_노선_조회();

        // Then
        assertThat(lineNames).doesNotContain(신분당선);
    }

    private static ExtractableResponse<Response> 노선_삭제(String createdId) {
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().delete(String.format("/lines/%s", createdId))
                        .then().log().all()
                        .extract();
        return response;
    }

    private static List<String> 모든_노선_조회() {
        List<String> lineNames =
                RestAssured.given().log().all()
                        .when().get("/lines")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);
        return lineNames;
    }

    static ExtractableResponse<Response> 노선_만들기(String name, String color, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        // when
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/lines")
                        .then().log().all()
                        .extract();
        return response;
    }

    static ExtractableResponse<Response> 아이디_노선_조회(String id) {
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get(String.format("/lines/%s", id))
                        .then().log().all()
                        .extract();
        return response;
    }

    private static ExtractableResponse<Response> 노선_수정(String createdId, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().put(String.format("/lines/%s", createdId))
                        .then().log().all()
                        .extract();
        return response;
    }
}
