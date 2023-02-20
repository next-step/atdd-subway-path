package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.*;

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
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청("2호선", "green");

        // then
        지하철_노선이_정상적으로_추가되었는지_확인(지하철_노선_생성_응답, "2호선");
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
        ExtractableResponse<Response> 지하철_노선_목록_조회_응답 = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록에_노선이_존재하는지_확인(지하철_노선_목록_조회_응답, "2호선", "3호선");
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
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> 지하철_노선_조회_응답 = 지하철_노선_조회_요청(지하철_노선_생성_응답);

        // then
        조회한_지하철_노선의_정보가_맞는지_확인(지하철_노선_조회_응답, "2호선");
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
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청("2호선", "green");

        // when
        지하철_노선_수정_요청(지하철_노선_생성_응답, "red");

        // then
        지하철_노선이_정상적으로_수정되었는지_확인(지하철_노선_생성_응답, "red");
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
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청("2호선", "green");

        // when
        final ExtractableResponse<Response> 지하철_노선_삭제_응답 = 지하철_노선_삭제_요청(지하철_노선_생성_응답);

        // then
        지하철이_정상적으로_삭제되었는지_확인(지하철_노선_삭제_응답);
    }
}
