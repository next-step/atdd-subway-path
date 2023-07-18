package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.handler.LineHandler.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> 생성된_지하철_노선 = 지하철_노선_생성_요청("2호선", "green");

        // then
        지하철_노선_생성됨(생성된_지하철_노선);

        ExtractableResponse<Response> 조회된_지하철_노선_목록 = 지하철_노선_목록_조회_요청();
        지하철_노선_조회됨(조회된_지하철_노선_목록);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        지하철_노선_생성_요청("2호선", "green");
        지하철_노선_생성_요청("3호선", "orange");

        // when
        ExtractableResponse<Response> 조회된_지하철_노선_목록 = 지하철_노선_목록_조회_요청();

        // then
        두개의_지하철_노선_조회됨(조회된_지하철_노선_목록);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> 생성된_지하철_노선 = 지하철_노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> 조회된_지하철_노선 = 지하철_노선_조회_요청(생성된_지하철_노선);

        // then
        생성된_지하철_노선_정보_응답됨(조회된_지하철_노선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> 생성된_지하철_노선 = 지하철_노선_생성_요청("2호선", "green");

        // when
        Map<String, String> 수정_목록 = new HashMap<>();
        수정_목록.put("color", "red");

        지하철_노선_정보_수정_요청(생성된_지하철_노선, 수정_목록);

        // then
        ExtractableResponse<Response> 수정된_지하철_노선 = 지하철_노선_조회_요청(생성된_지하철_노선);
        지하철_노선_수정됨(수정된_지하철_노선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> 생성된_지하철_노선 = 지하철_노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> 지하철_노선_삭제_결과 = 지하철_노선_삭제_요청(생성된_지하철_노선);

        // then
        지하철_노선_삭제됨(지하철_노선_삭제_결과);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getList("name")).contains("2호선");
    }

    private void 두개의_지하철_노선_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).contains("2호선", "3호선");
    }

    private void 생성된_지하철_노선_정보_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("color")).isEqualTo("red");
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
