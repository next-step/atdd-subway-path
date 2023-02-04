package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private static final String 이호선 = "2호선";
    private static final String 삼호선 = "3호선";
    private static final String 녹색 = "bg-green-600";
    private static final String 빨간색 = "bg-red-600";

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        final ExtractableResponse<Response> 이호선_응답 = 지하철_노선_생성_요청(이호선, 녹색);

        final ExtractableResponse<Response> 지하철_노선_목록_조회_응답 = 지하철_노선_목록_조회_요청();

        지하철_노선_목록_중_생성한_노선_조회됨(지하철_노선_목록_조회_응답, 이호선_응답);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        지하철_노선_생성_요청(이호선, 녹색);
        지하철_노선_생성_요청(삼호선, 빨간색);

        final ExtractableResponse<Response> 지하철_노선_목록_응답 = 지하철_노선_목록_조회_요청();

        지하철_노선_목록_조회됨(지하철_노선_목록_응답, 2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(이호선, 녹색);

        final ExtractableResponse<Response> 지하철_노선_조회_응답 = 지하철_노선_조회_요청(createResponse);

        지하철_노선_조회됨(지하철_노선_조회_응답, 이호선, 녹색, 0);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(이호선, 녹색);

        final Map<String, String> params = 지하철_노선_수정(삼호선, 빨간색);
        지하철_노선_정보_수정_요청(createResponse, params);

        final ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(삼호선),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo(빨간색)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(이호선, 녹색);

        final ExtractableResponse<Response> response = 지하철_노선_삭제_요청(createResponse);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> 지하철_노선_수정(final String name, final String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }
}
