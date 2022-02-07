package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    Map<String, String> 이호선;

    /**
     * Given 지하철역을 두개 생성하고 노선에 대한 파라미터를 생성한 후
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        Long 강남역_번호 = StationSteps.지하철역_생성_요청("강남역").jsonPath().getLong("id");
        Long 역삼역_번호 = StationSteps.지하철역_생성_요청("역삼역").jsonPath().getLong("id");

        이호선 = Map.of("name", "2호선",
                "color", "green",
                "upStationId", 강남역_번호.toString(),
                "downStationId", 역삼역_번호.toString(),
                "distance", String.valueOf(100)
        );
    }

    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(이호선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * Given 새로운 지하철 노선 생성을 요청 하고
     * When 지하철 노선 목록 조회를 요청 하면
     * Then 두 노선이 포함된 지하철 노선 목록을 응답받는다
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        Long 신사역_번호 = StationSteps.지하철역_생성_요청("신사역").jsonPath().getLong("id");
        Long 광교역_번호 = StationSteps.지하철역_생성_요청("광교역").jsonPath().getLong("id");

        Map<String, String> 신분당선 = Map.of("name", "신분당선",
                "color", "red",
                "upStationId", 신사역_번호.toString(),
                "downStationId", 광교역_번호.toString(),
                "distance", String.valueOf(100)
        );

        지하철_노선_생성_요청(이호선);
        지하철_노선_생성_요청(신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).contains("2호선", "신분당선");
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 조회를 요청 하면
     * Then 생성한 지하철 노선을 응답받는다
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(이호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 지하철 노선의 정보 수정을 요청 하면
     * Then 지하철 노선의 정보 수정은 성공한다.
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(이호선);

        // when
        Map<String, String> params = new HashMap<>();
        params.put("color", "red");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(createResponse.header("location"))
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 삭제를 요청 하면
     * Then 생성한 지하철 노선 삭제가 성공한다.
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(이호선);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete(createResponse.header("location"))
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 같은 이름으로 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 실패한다.
     */
    @DisplayName("중복이름으로 지하철 노선 생성")
    @Test
    void duplicateName() {
        // given
        지하철_노선_생성_요청(이호선);

        // when
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(이호선);

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
