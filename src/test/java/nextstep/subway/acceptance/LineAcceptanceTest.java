package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.dto.LineTestRequest;
import nextstep.subway.acceptance.step.LineTestStep;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // given
        LineTestRequest lineTestRequest = LineTestStep.지하철_노선_요청_신분당선_데이터_생성하기();

        // when
        ExtractableResponse<Response> response = LineTestStep.지하철_노선을_생성한다(lineTestRequest);

        // then
        LineTestStep.지하철_노선_생성_성공_검증하기(response);
    }

    @DisplayName("중복이름으로 지하철 노선 생성")
    @Test
    void createDuplicatedNameLine() {
        // given
        LineTestRequest lineTestRequest = LineTestStep.지하철_노선_요청_신분당선_데이터_생성하기();
        LineTestStep.지하철_노선을_생성한다(lineTestRequest);

        // when
        ExtractableResponse<Response> response = LineTestStep.지하철_노선을_생성한다(lineTestRequest);

        // then
        LineTestStep.지하철_노선_중복이름_생성_실패_검증하기(response);
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        LineTestRequest 이호선_요청 = LineTestStep.지하철_노선_요청_이호선_데이터_생성하기();
        LineTestStep.지하철_노선을_생성한다(이호선_요청);
        LineTestRequest 신분당선_요청 = LineTestStep.지하철_노선_요청_신분당선_데이터_생성하기();
        LineTestStep.지하철_노선을_생성한다(신분당선_요청);

        // when
        ExtractableResponse<Response> response = LineTestStep.지하철_노선_목록을_조회한다();

        // then
        LineTestStep.지하철_노선_목록_조회_시_두_노선이_있는지_검증하기(response, Arrays.asList(이호선_요청, 신분당선_요청));
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        LineTestRequest 신분당선_요청 = LineTestStep.지하철_노선_요청_신분당선_데이터_생성하기();
        Long 신분당선_생성_아이디 = LineTestStep.지하철_노선_생성한_후_아이디_추출하기(신분당선_요청);

        // when
        ExtractableResponse<Response> response = LineTestStep.지하철_노선을_조회한다(신분당선_생성_아이디);

        // then
        LineTestStep.지하철_노선_조회_성공_검증하기(response, 신분당선_생성_아이디, 신분당선_요청);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        LineTestRequest 신분당선_요청 = LineTestStep.지하철_노선_요청_신분당선_데이터_생성하기();
        Long 신분당선_생성_아이디 = LineTestStep.지하철_노선_생성한_후_아이디_추출하기(신분당선_요청);
        String 수정_색 = "bg-red-400";
        String 수정_이름 = "신분당선_연장";

        // when
        ExtractableResponse<Response> response = LineTestStep.지하철_노선을_수정한다(신분당선_생성_아이디, 수정_색, 수정_이름);

        // then
        LineTestStep.지하철_노선_수정_성공_검증하기(response, 신분당선_생성_아이디, 수정_색, 수정_이름);
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        LineTestRequest 신분당선_요청 = LineTestStep.지하철_노선_요청_신분당선_데이터_생성하기();
        Long 신분당선_생성_아이디 = LineTestStep.지하철_노선_생성한_후_아이디_추출하기(신분당선_요청);

        // when
        ExtractableResponse<Response> response = LineTestStep.지하철_노선을_삭제한다(신분당선_생성_아이디);

        // then
        LineTestStep.지하철_노선_삭제_성공_검증하기(response);
    }
}
