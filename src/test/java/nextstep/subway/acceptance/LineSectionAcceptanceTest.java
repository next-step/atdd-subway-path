package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineFixture.*;
import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationFixture.*;
import static nextstep.subway.acceptance.StationSteps.지하철_역_생성_되어있음;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {

    private Long 신분당선_번호;
    private Long 강남역_번호;
    private Long 양재역_번호;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void 지하철_역과_노선이_생성되어_있음() {
        super.setUp();

        강남역_번호 = 지하철_역_생성_되어있음(강남역);
        양재역_번호 = 지하철_역_생성_되어있음(양재역);

        신분당선_번호 = 지하철_노선이_생성되어_있음(신분당선, 빨강색, 강남역_번호, 양재역_번호, 강남_양재_거리);
    }

    /**
     * When 지하철 노선 하행 좀점역과 연결되는 새로운 구간 추가를 요청 하면
     * Then 지하철 노선에 새로운 구간이 등록된다.
     */
    @DisplayName("지하철 노선에 하행 좀점역과 연결되는 새로운 구간을 등록")
    @Test
    void addLineSectionByDownStation() {
        // when
        final Long 정자역_번호 = 지하철_역_생성_되어있음(정자역);
        지하철_노선_구간_등록을_요청한다(신분당선_번호, 양재역_번호, 정자역_번호, 양재_정자_거리);

        // then
        final ExtractableResponse<Response> response = 지하철_노선_조회를_요청한다(신분당선_번호);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_번호, 양재역_번호, 정자역_번호)
        );
    }

    /**
     * When 지하철 노선 상행 종점역과 연결되는 새로운 구간 추가를 요청 하면
     * Then 지하철 노선에 새로운 구간이 등록된다.
     */
    @DisplayName("지하철 노선에 상행 종점역과 연결되는 새로운 구간을 등록")
    @Test
    void addLineSectionByUpStation() {
        // when
        final Long 논현역_번호 = 지하철_역_생성_되어있음("논현역");
        지하철_노선_구간_등록을_요청한다(신분당선_번호, 논현역_번호, 강남역_번호, 논현_강남_거리);

        // then
        final ExtractableResponse<Response> response = 지하철_노선_조회를_요청한다(신분당선_번호);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(논현역_번호, 강남역_번호, 양재역_번호)
        );
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역_번호 = 지하철_역_생성_되어있음(정자역);
        지하철_노선_구간_등록을_요청한다(신분당선_번호, 양재역_번호, 정자역_번호, 양재_정자_거리);

        // when
        지하철_노선_구간을_삭제_요청한다(신분당선_번호, 정자역_번호);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회를_요청한다(신분당선_번호);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_번호, 양재역_번호)
        );
    }
}
