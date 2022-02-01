package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.acceptance.LineFixture.*;
import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationFixture.*;
import static nextstep.subway.acceptance.StationSteps.지하철_역_생성_되어있음;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private Long 강남역_번호;
    private Long 판교역_번호;
    private Long 사당역_번호;
    private Long 낙성대역_번호;

    /**
     * Given 지하철역 생성을 요청 하고
     */
    @BeforeEach
    void 지하철_역이_생성_되어있음() {
        super.setUp();

        강남역_번호 = 지하철_역_생성_되어있음(강남역);
        판교역_번호 = 지하철_역_생성_되어있음(판교역);
        사당역_번호 = 지하철_역_생성_되어있음(사당역);
        낙성대역_번호 = 지하철_역_생성_되어있음(낙성대역);
    }

    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성을_요청한다(신분당선, 빨강색, 강남역_번호, 판교역_번호, 강남_판교_거리);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
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
        지하철_노선_생성을_요청한다(신분당선, 빨강색, 강남역_번호, 판교역_번호, 강남_판교_거리);
        지하철_노선_생성을_요청한다(이호선, 초록색, 사당역_번호, 낙성대역_번호, 사당_낙성대_거리);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_목록_조회를_요청한다();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE),
                () -> assertThat(response.header("Date")).isNotBlank(),
                () -> assertThat(response.jsonPath().getList("id")).contains(1, 2),
                () -> assertThat(response.jsonPath().getList(LINE_NAME)).contains(신분당선, 이호선),
                () -> assertThat(response.jsonPath().getList(LINE_COLOR)).contains(빨강색, 초록색)
        );
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
        final Long 신분당선_번호 = 지하철_노선이_생성되어_있음(신분당선, 빨강색, 강남역_번호, 판교역_번호, 강남_판교_거리);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_조회를_요청한다(신분당선_번호);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE),
                () -> assertThat(response.header("Date")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().get("id").equals(1)).isTrue(),
                () -> assertThat(response.body().jsonPath().get(LINE_NAME).equals(신분당선)).isTrue(),
                () -> assertThat(response.body().jsonPath().get(LINE_COLOR).equals(빨강색)).isTrue(),
                () -> assertThat(response.jsonPath().getList("stations.name")).contains(강남역, 판교역)
        );
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
        final Long 신분당선_번호 = 지하철_노선이_생성되어_있음(신분당선, 빨강색, 강남역_번호, 판교역_번호, 강남_판교_거리);

        // when
        final ExtractableResponse<Response> updateResponse = 지하철_노선_변경을_요청한다(신분당선_번호, 구분당선, 파랑색);

        // then
        assertAll(
                () -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(updateResponse.header("Date")).isNotBlank()
        );
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
        final Long 신분당선_번호 = 지하철_노선이_생성되어_있음(신분당선, 빨강색, 강남역_번호, 판교역_번호, 강남_판교_거리);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_삭제를_요청한다(신분당선_번호);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(response.header("Date")).isNotBlank()
        );
    }
}
