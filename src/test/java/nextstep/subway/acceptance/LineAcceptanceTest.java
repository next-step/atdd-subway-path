package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("2호선", "green");

        // then
        요청_응답_확인(response, HttpStatus.CREATED);
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
        지하철_노선_생성_요청("2호선", "green");
        지하철_노선_생성_요청("3호선", "orange");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        요청_응답_확인(response, HttpStatus.OK);
        assertThat(response.jsonPath().getList("name")).contains("2호선", "3호선");
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        // then
        요청_응답_확인(response, HttpStatus.OK);
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("2호선", "green");

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
        요청_응답_확인(response, HttpStatus.OK);
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().delete(createResponse.header("location"))
            .then().log().all().extract();

        // then
        요청_응답_확인(response, HttpStatus.NO_CONTENT);
    }

    @DisplayName("중복이름으로 지하철 노선 생성")
    @Test
    void duplicateName() {
        // given
        지하철_노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("2호선", "green");

        // then
        요청_응답_확인(createResponse, HttpStatus.BAD_REQUEST);
    }
}
