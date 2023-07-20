package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.steps.LineSteps.*;
import static nextstep.subway.acceptance.steps.StationSteps.역_생성_ID_추출;
import static nextstep.subway.acceptance.steps.StationSteps.지하철역_생성_요청;
import static nextstep.subway.fixture.LineFixture.*;
import static nextstep.subway.fixture.StationFixture.*;
import static nextstep.subway.utils.CustomAssertions.상태코드_확인;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    Long 강남역_ID;
    Long 역삼역_ID;
    Long 선릉역_ID;

    @BeforeEach
    void setup() {
        강남역_ID = 역_생성_ID_추출(지하철역_생성_요청(강남역));
        역삼역_ID = 역_생성_ID_추출(지하철역_생성_요청(역삼역));
        선릉역_ID = 역_생성_ID_추출(지하철역_생성_요청(선릉역));
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(분당선, 빨간색);

        // then
        상태코드_확인(response, HttpStatus.CREATED);
        assertThat((노선_이름_목록_추출(지하철_노선_목록_조회_요청()))).contains(분당선);

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
        지하철_노선_생성_요청(분당선, 노란색);
        지하철_노선_생성_요청(신분당선, 빨간색);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        상태코드_확인(response, HttpStatus.OK);
        assertThat(노선_이름_목록_추출(response)).contains(분당선, 신분당선);
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
        Long createdId = 노선_생성_ID_추출(지하철_노선_생성_요청(분당선, 노란색));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdId);

        // then
        상태코드_확인(response, HttpStatus.OK);
        assertThat(노선_이름_추출(response)).isEqualTo(분당선);
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
        Long createdId = 노선_생성_ID_추출(지하철_노선_생성_요청(분당선, 노란색));

        // when
        지하철_노선_수정_요청(createdId, 신분당선, 빨간색);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdId);
        상태코드_확인(response, HttpStatus.OK);
        assertThat(노선_이름_추출(response)).isEqualTo(신분당선);
        assertThat(노선_색깔_추출(response)).isEqualTo(빨간색);
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
        Long createdId = 노선_생성_ID_추출(지하철_노선_생성_요청(분당선, 노란색));

        // when
        ExtractableResponse<Response> response = 노선_삭제_요청(createdId);

        // then
        상태코드_확인(response, HttpStatus.NO_CONTENT);
    }
}
