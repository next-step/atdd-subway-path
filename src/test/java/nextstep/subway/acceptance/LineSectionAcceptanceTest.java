package nextstep.subway.acceptance;

import static nextstep.subway.support.fixture.LineFixture.강남역_교대역_구간_이호선_생성_요청;
import static nextstep.subway.support.fixture.LineFixture.강남역_봉천역_구간_이호선_생성_요청;
import static nextstep.subway.support.fixture.SectionFixture.구간_생성_요청;
import static nextstep.subway.support.fixture.StationFixture.낙성대역_생성_요청;
import static nextstep.subway.support.fixture.StationFixture.봉천역_생성_요청;
import static nextstep.subway.support.fixture.StationFixture.서울대입구역_생성_요청;
import static nextstep.subway.support.step.LineSteps.지하철_노선_단일_조회_요청;
import static nextstep.subway.support.step.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.support.step.LineSteps.지하철_노선_응답에서_노선_아이디_추출;
import static nextstep.subway.support.step.LineSteps.지하철_노선_응답에서_노선의_상행_종점역_아이디_추출;
import static nextstep.subway.support.step.LineSteps.지하철_노선_응답에서_노선의_하행_종점역_아이디_추출;
import static nextstep.subway.support.step.LineSteps.지하철_노선_응답에서_역_아이디_목록_추출;
import static nextstep.subway.support.step.SectionSteps.지하철_구간_등록_요청;
import static nextstep.subway.support.step.SectionSteps.지하철_구간_삭제_요청;
import static nextstep.subway.support.step.StationSteps.지하철_역_생성_요청;
import static nextstep.subway.support.step.StationSteps.지하철역_응답에서_역_아이디_추출;
import static org.assertj.core.api.Assertions.assertThat;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.support.annotation.AcceptanceTest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;


@DisplayName("지하철 구간 관련 기능")
@AcceptanceTest
class LineSectionAcceptanceTest {


    /*
    Given 지하철 노선이 있을 때
    When 구간을 노선에 등록하면
    Then 구간이 노선에 등록되어야 한다.
    Then 노선의 하행역이 구간의 하행역으로 바뀌어야 한다.
     */
    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        ExtractableResponse<Response> 강남_교대_이호선_응답 = 지하철_노선_생성_요청(강남역_교대역_구간_이호선_생성_요청());
        Long 이호선_아이디 = 지하철_노선_응답에서_노선_아이디_추출(강남_교대_이호선_응답);
        Long 교대역_아이디 = 지하철_노선_응답에서_노선의_하행_종점역_아이디_추출(강남_교대_이호선_응답);
        Long 봉천역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(봉천역_생성_요청()));

        // when
        ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(이호선_아이디, 구간_생성_요청(
            교대역_아이디,
            봉천역_아이디,
            10L
        ));

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_구간_등록_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(지하철_노선_응답에서_노선의_하행_종점역_아이디_추출(지하철_노선_단일_조회_요청(이호선_아이디))).isEqualTo(봉천역_아이디);
        });

    }

    /*
     * Given 지하철 노선이 있을 때
     * When 구간의 상행역이 노선의 하행역과 다른 구간을 노선에 등록하면
     * Then 구간이 노선에 등록되지 않고 500 에러를 발생한다.
     */
    @DisplayName("지하철 구간의 상행역이 노선의 하행역이 아닌경우 등록에 실패한다.")
    @Test
    void addSectionWithInvalidUpStation() {
        // given
        ExtractableResponse<Response> 강남_교대_이호선_응답 = 지하철_노선_생성_요청(강남역_교대역_구간_이호선_생성_요청());
        Long 이호선_아이디 = 지하철_노선_응답에서_노선_아이디_추출(강남_교대_이호선_응답);
        Long 낙성대역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(낙성대역_생성_요청()));
        Long 봉천역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(봉천역_생성_요청()));

        // when
        ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(이호선_아이디, 구간_생성_요청(
            낙성대역_아이디,
            봉천역_아이디,
            10L
        ));

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_구간_등록_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        });
    }


    /*
     * Given 지하철 노선이 있을 때
     * When 노선에 이미 등록되어 있는 역을 구간의 하행역으로 등록하면
     * Then 구간이 노선에 등록되지 않고 500 에러를 발생한다.
     */
    @DisplayName("지하철 구간의 하행역이 노선에 이미 등록되어 있는 역일 경우 등록에 실패한다.")
    @Test
    void addSectionWithAlreadyExistsStation() {
        // given
        ExtractableResponse<Response> 강남_교대_이호선_응답 = 지하철_노선_생성_요청(강남역_교대역_구간_이호선_생성_요청());
        Long 이호선_아이디 = 지하철_노선_응답에서_노선_아이디_추출(강남_교대_이호선_응답);
        Long 교대역_아이디 = 지하철_노선_응답에서_노선의_하행_종점역_아이디_추출(강남_교대_이호선_응답);
        Long 봉천역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(봉천역_생성_요청()));
        지하철_구간_등록_요청(이호선_아이디, 구간_생성_요청(교대역_아이디, 봉천역_아이디, 10L));

        Long 서울대입구역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(서울대입구역_생성_요청()));

        // when
        ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(이호선_아이디, 구간_생성_요청(
            서울대입구역_아이디,
            봉천역_아이디,
            10L
        ));

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_구간_등록_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        });

    }

    /*
     * Given 지하철 노선에 구간이 존재하고,  ex) (a - b)
     * When 해당 노선에 역을 중간에 추가하면, ex) (a - c)
     * Then 기존 구간의 상행역을 기준으로 새로운 구간이 등록된다. ex) (a - c- b)
     */
    @DisplayName("지하철 노선 중간에 역을 추가한다.")
    @Test
    void addSectionWithIndex() {
        // given
        ExtractableResponse<Response> 강남_봉천_이호선_응답 = 지하철_노선_생성_요청(강남역_봉천역_구간_이호선_생성_요청(10L));
        Long 이호선_아이디 = 지하철_노선_응답에서_노선_아이디_추출(강남_봉천_이호선_응답);
        Long 강남역_아이디 = 지하철_노선_응답에서_노선의_상행_종점역_아이디_추출(강남_봉천_이호선_응답);
        Long 봉천역_아이디 = 지하철_노선_응답에서_노선의_하행_종점역_아이디_추출(강남_봉천_이호선_응답);
        Long 서울대입구역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(서울대입구역_생성_요청()));

        // when
        ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(이호선_아이디, 구간_생성_요청(
            강남역_아이디,
            서울대입구역_아이디,
            5L
        ));

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_구간_등록_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(지하철_노선_응답에서_역_아이디_목록_추출(지하철_노선_단일_조회_요청(이호선_아이디))).containsExactly(강남역_아이디, 서울대입구역_아이디, 봉천역_아이디);
        });

    }


    /*
    Given 지하철 노선이 있을 때
    When 노선에 등록된 하행 종점역을 삭제하면
    Then 마지막 구간이 노선에서 제거된다.
     */
    @DisplayName("지하철 노선에 구간을 제거한다.")
    @Test
    void removeSection() {
        // given
        ExtractableResponse<Response> 강남_교대_이호선_응답 = 지하철_노선_생성_요청(강남역_교대역_구간_이호선_생성_요청());
        Long 이호선_아이디 = 지하철_노선_응답에서_노선_아이디_추출(강남_교대_이호선_응답);
        Long 교대역_아이디 = 지하철_노선_응답에서_노선의_하행_종점역_아이디_추출(강남_교대_이호선_응답);
        Long 봉천역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(봉천역_생성_요청()));
        지하철_구간_등록_요청(이호선_아이디, 구간_생성_요청(교대역_아이디, 봉천역_아이디, 10L));

        // when
        ExtractableResponse<Response> 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(이호선_아이디, 봉천역_아이디);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_구간_삭제_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(지하철_노선_응답에서_역_아이디_목록_추출(지하철_구간_삭제_응답)).doesNotContain(봉천역_아이디);
        });

    }

    /*
    Given 지하철 노선이 있을 때
    When 지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우)
    Then 구간이 노선에 등록되어야 한다.
    Then 노선의 하행역이 구간의 하행역으로 바뀌어야 한다.
     */
    @DisplayName("지하철 노선에 구간이 하나인 경우 해당 구간을 제거할 수 없다.")
    @Test
    void removeSectionWithSectionSizeIsOne() {
        // given
        ExtractableResponse<Response> 강남_교대_이호선_응답 = 지하철_노선_생성_요청(강남역_교대역_구간_이호선_생성_요청());
        Long 이호선_아이디 = 지하철_노선_응답에서_노선_아이디_추출(강남_교대_이호선_응답);
        Long 교대역_아이디 = 지하철_노선_응답에서_노선의_하행_종점역_아이디_추출(강남_교대_이호선_응답);

        // when
        ExtractableResponse<Response> 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(이호선_아이디, 교대역_아이디);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_구간_삭제_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        });

    }

    /*
    Given 지하철 노선이 있을 때
    When 지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우)
    Then 구간이 노선에 등록되어야 한다.
    Then 노선의 하행역이 구간의 하행역으로 바뀌어야 한다.
     */
    @DisplayName("지하철 노선에 마지막 구간이 아닌 경우 구간을 제거할 수 없다.")
    @Test
    void removeSectionWithIsNotLastSection() {
        // given
        ExtractableResponse<Response> 강남_교대_이호선_응답 = 지하철_노선_생성_요청(강남역_교대역_구간_이호선_생성_요청());
        Long 이호선_아이디 = 지하철_노선_응답에서_노선_아이디_추출(강남_교대_이호선_응답);
        Long 교대역_아이디 = 지하철_노선_응답에서_노선의_하행_종점역_아이디_추출(강남_교대_이호선_응답);
        Long 봉천역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(봉천역_생성_요청()));
        지하철_구간_등록_요청(이호선_아이디, 구간_생성_요청(교대역_아이디, 봉천역_아이디, 10L));

        // when
        ExtractableResponse<Response> 지하철_구간_삭제_응답 = 지하철_구간_삭제_요청(이호선_아이디, 교대역_아이디);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_구간_삭제_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        });

    }

}
