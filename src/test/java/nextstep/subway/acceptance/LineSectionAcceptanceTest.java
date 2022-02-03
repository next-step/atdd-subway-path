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
    private Long 판교역_번호;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void 지하철_역과_노선이_생성되어_있음() {
        super.setUp();

        강남역_번호 = 지하철_역_생성_되어있음(강남역);
        판교역_번호 = 지하철_역_생성_되어있음(판교역);

        신분당선_번호 = 지하철_노선이_생성되어_있음(신분당선, 빨강색, 강남역_번호, 판교역_번호, 강남_판교_거리);
    }

    /**
     * When 지하철 노선에 새로운 하행 종점역 구간 등록을 요청 하면
     * Then 지하철 노선에 새로운 구간이 등록된다.
     * 예시) 강남 판교 - 판교-정자
     */
    @DisplayName("지하철 노선에 새로운 하행 종점역 구간을 등록")
    @Test
    void addLineSectionExtensionDownTerminalStation() {
        // when
        final Long 정자역_번호 = 지하철_역_생성_되어있음(정자역);
        지하철_노선_구간_등록을_요청한다(신분당선_번호, 판교역_번호, 정자역_번호, 판교_정자_거리);

        final ExtractableResponse<Response> response = 지하철_노선_조회를_요청한다(신분당선_번호);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_번호, 판교역_번호, 정자역_번호)
        );
    }

    /**
     * When 지하철 노선에 새로운 상행 종점역 구간 등록을 요청 하면
     * Then 지하철 노선에 새로운 구간이 등록된다.
     * 예시) 강남 판교 - 논현 강남
     */
    @DisplayName("지하철 노선에 새로운 상행 종점역 구간을 등록")
    @Test
    void addLineSectionExtensionUpTerminalStation() {
        // when
        final Long 논현역_번호 = 지하철_역_생성_되어있음(논현역);
        지하철_노선_구간_등록을_요청한다(신분당선_번호, 논현역_번호, 강남역_번호, 논현_강남_거리);

        final ExtractableResponse<Response> response = 지하철_노선_조회를_요청한다(신분당선_번호);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(논현역_번호, 강남역_번호, 판교역_번호)
        );
    }

    /***
     * When 지하철 노선 하행을 기준으로 중앙 방향의 새로운 구간 등록을 요청 하면
     * Then 지하철 노선에 새로운 구간이 등록된다.
     * 예시) 강남 판교 - 양재 판교
     */
    @DisplayName("지하철 노선 하행을 기준으로 중앙 방향의 새로운 구간을 등록")
    @Test
    void addLineSectionDownStation() {
        // when
        final Long 양재역_번호 = 지하철_역_생성_되어있음(양재역);
        지하철_노선_구간_등록을_요청한다(신분당선_번호, 양재역_번호, 판교역_번호, 양재_판교_거리);

        final ExtractableResponse<Response> response = 지하철_노선_조회를_요청한다(신분당선_번호);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_번호, 양재역_번호, 판교역_번호)
        );
    }

    /***
     * When 지하철 노선 상행을 기준으로 중앙 방향의 새로운 구간 등록을 요청 하면
     * Then 지하철 노선에 새로운 구간이 등록된다.
     * 예시) 강남 판교 - 강남 양재
     */
    @DisplayName("지하철 노선 상행을 기준으로 중앙 방향의 새로운 구간을 등록")
    @Test
    void addLineSectionUpStation() {
        // when
        final Long 양재역_번호 = 지하철_역_생성_되어있음(양재역);
        지하철_노선_구간_등록을_요청한다(신분당선_번호, 강남역_번호, 양재역_번호, 강남_양재_거리);

        final ExtractableResponse<Response> response = 지하철_노선_조회를_요청한다(신분당선_번호);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_번호, 양재역_번호, 판교역_번호)
        );
    }

    /***
     * When 하행을 기준으로 지하철 노선에 있는 역 사이 길이보다 같거나 긴 새로운 구간 등록 요청을 하면
     * Then 지하철 노선에 새로운 구간 등록이 실패한다.
     * 예시) 강남 판교 - 양재 판교
     */
    @DisplayName("하행을 기준으로 지하철 노선에 있는 역 사이 길이보다 같거나 긴 새로운 지하철 노선 구간 등록")
    @Test
    void addLineSectionDownStationException() {
        // when
        final Long 양재역_번호 = 지하철_역_생성_되어있음(양재역);
        final int 비정상적인_양재_판교_거리 = 10;
        final ExtractableResponse<Response> response = 지하철_노선_구간_등록을_요청한다(신분당선_번호, 양재역_번호, 판교역_번호, 비정상적인_양재_판교_거리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /***
     * When 상행을 기준으로 지하철 노선에 있는 역 사이 길이보다 같거나 긴 새로운 구간 등록 요청을 하면
     * Then 지하철 노선에 새로운 구간 등록이 실패한다.
     * 예시) 강남 판교 - 강남 양재
     */
    @DisplayName("상행을 기준으로 지하철 노선에 있는 역 사이 길이보다 같거나 긴 새로운 지하철 노선 구간 등록")
    @Test
    void addLineSectionByUpStationException() {
        // when
        final Long 양재역_번호 = 지하철_역_생성_되어있음(양재역);
        final int 비정상적인_강남_양재_거리 = 10;
        final ExtractableResponse<Response> response = 지하철_노선_구간_등록을_요청한다(신분당선_번호, 강남역_번호, 양재역_번호, 비정상적인_강남_양재_거리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /***
     * When 지하철 노선에 기존 구간에 있는 역들로 새로운 구간 등록 요청을 하면
     * Then 지하철 노선에 새로운 구간 추가가 실패한다.
     */
    @DisplayName("지하철 노선에 기존 구간에 있는 역들로 새로운 구간 등록")
    @Test
    void addLineSectionSameSectionException() {
        // when
        final ExtractableResponse<Response> response = 지하철_노선_구간_등록을_요청한다(신분당선_번호, 강남역_번호, 판교역_번호, 강남_판교_거리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 기존 등록되지 않은 역으로 새로운 구간 등록 요청을 하면
     * Then 지하철 노선에 새로운 구간 추가가 실패한다.
     */
    @DisplayName("지하철 노선에 기존 등록되지 않은 역으로 새로운 지하철 노선 구간 등록")
    @Test
    void addLineAnyStationNotContainException() {
        // when
        final Long 논현역_번호 = 지하철_역_생성_되어있음(논현역);
        final Long 양재역_번호 = 지하철_역_생성_되어있음(양재역);

        final ExtractableResponse<Response> response = 지하철_노선_구간_등록을_요청한다(신분당선_번호, 논현역_번호, 양재역_번호, 논현_양재_거리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
        지하철_노선_구간_등록을_요청한다(신분당선_번호, 판교역_번호, 정자역_번호, 판교_정자_거리);

        // when
        지하철_노선_구간을_삭제_요청한다(신분당선_번호, 정자역_번호);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회를_요청한다(신분당선_번호);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_번호, 판교역_번호)
        );
    }

    /***
     * Given 새로운 지하철 노선 구간을 등록하고
     * When 지하철 노선에 등록되지 않은 역을 기준으로 구간 삭제 요청 하면
     * Then 지하철 노선에 구간이 삭제가 실패한다.
     */
    @DisplayName("지하철 노선에 등록되지 않은 역을 기준으로 구간을 삭제한다.")
    @Test
    void removeExcludeStationException() {
        // given
        final Long 정자역_번호 = 지하철_역_생성_되어있음(정자역);
        final Long 논현역_번호 = 지하철_역_생성_되어있음(논현역);
        지하철_노선_구간_등록을_요청한다(신분당선_번호, 판교역_번호, 정자역_번호, 판교_정자_거리);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_구간을_삭제_요청한다(신분당선_번호, 논현역_번호);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
