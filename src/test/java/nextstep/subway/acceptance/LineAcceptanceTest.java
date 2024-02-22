package nextstep.subway.acceptance;

import static nextstep.subway.support.fixture.LineFixture.강남역_교대역_구간_이호선_생성_요청;
import static nextstep.subway.support.fixture.LineFixture.노선_생성_요청;
import static nextstep.subway.support.fixture.LineFixture.노선_수정_요청;
import static nextstep.subway.support.fixture.LineFixture.서울역_청량리역_구간_일호선_생성_요청;
import static nextstep.subway.support.fixture.LineFixture.이호선_이름;
import static nextstep.subway.support.fixture.LineFixture.일호선_이름;
import static nextstep.subway.support.fixture.StationFixture.강남역_생성_요청;
import static nextstep.subway.support.fixture.StationFixture.교대역_생성_요청;
import static nextstep.subway.support.step.LineSteps.지하철_노선_단일_조회_요청;
import static nextstep.subway.support.step.LineSteps.지하철_노선_목록_응답에서_노선_아이디_목록_추출;
import static nextstep.subway.support.step.LineSteps.지하철_노선_목록_응답에서_노선_이름_목록_추출;
import static nextstep.subway.support.step.LineSteps.지하철_노선_목록_조회_요청;
import static nextstep.subway.support.step.LineSteps.지하철_노선_삭제_요청;
import static nextstep.subway.support.step.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.support.step.LineSteps.지하철_노선_수정_요청;
import static nextstep.subway.support.step.LineSteps.지하철_노선_응답에서_노선_색상_추출;
import static nextstep.subway.support.step.LineSteps.지하철_노선_응답에서_노선_아이디_추출;
import static nextstep.subway.support.step.LineSteps.지하철_노선_응답에서_노선_이름_추출;
import static nextstep.subway.support.step.StationSteps.지하철_역_생성_요청;
import static nextstep.subway.support.step.StationSteps.지하철역_응답에서_역_아이디_추출;
import static org.assertj.core.api.Assertions.assertThat;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.support.annotation.AcceptanceTest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
@AcceptanceTest
class LineAcceptanceTest {


    public static final String LINE_TWO = "2호선";
    public static final String COLOR_ONE = "1호선 노선색";
    public static final String COLOR_TWO = "2호선 노선색";
    public static final Long DISTANCE = 100L;


    /**
     * Given 지하철 역이 2개 존재하고
     * When 지하철 노선을 생성하면
     * Then 지하철 노선이 생성된다.
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Long 강남역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(강남역_생성_요청()));
        Long 교대역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(교대역_생성_요청()));

        // when
        ExtractableResponse<Response> 지하철_라인_생성_응답 = 지하철_노선_생성_요청(
            노선_생성_요청(
                LINE_TWO,
                COLOR_ONE,
                강남역_아이디,
                교대역_아이디,
                DISTANCE
            ));

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_라인_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(지하철_노선_목록_응답에서_노선_이름_목록_추출(지하철_노선_목록_조회_요청())).containsAnyOf(LINE_TWO);
        });

    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLineList() {
        // given
        지하철_노선_생성_요청(서울역_청량리역_구간_일호선_생성_요청());
        지하철_노선_생성_요청(강남역_교대역_구간_이호선_생성_요청());

        // when
        ExtractableResponse<Response> 지하철_노선_목록_조회_응답 = 지하철_노선_목록_조회_요청();

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_노선_목록_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(지하철_노선_목록_조회_응답.body().as(List.class)).hasSize(2);
            assertThat(지하철_노선_목록_응답에서_노선_이름_목록_추출(지하철_노선_목록_조회_응답)).containsAnyOf(일호선_이름, 이호선_이름);
        });

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Long 일호선_아이디 = 지하철_노선_응답에서_노선_아이디_추출(지하철_노선_생성_요청(서울역_청량리역_구간_일호선_생성_요청()));

        // when
        ExtractableResponse<Response> 지하철_노선_단일_조희_응답 = 지하철_노선_단일_조회_요청(일호선_아이디);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_노선_단일_조희_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(지하철_노선_응답에서_노선_아이디_추출(지하철_노선_단일_조희_응답)).isEqualTo(일호선_아이디);
        });

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Long 일호선_아이디 = 지하철_노선_응답에서_노선_아이디_추출(지하철_노선_생성_요청(서울역_청량리역_구간_일호선_생성_요청()));
        // when
        ExtractableResponse<Response> 지하철_노선_수정_응답 = 지하철_노선_수정_요청(일호선_아이디, 노선_수정_요청(LINE_TWO, COLOR_TWO));

        // then
        ExtractableResponse<Response> 지하철_노선_단일_조희_응답 = 지하철_노선_단일_조회_요청(일호선_아이디);
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_노선_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(지하철_노선_응답에서_노선_이름_추출(지하철_노선_단일_조희_응답)).isEqualTo(LINE_TWO);
            assertThat(지하철_노선_응답에서_노선_색상_추출(지하철_노선_단일_조희_응답)).isEqualTo(COLOR_TWO);
        });

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteStation() {
        // given
        Long 일호선_아이디 = 지하철_노선_응답에서_노선_아이디_추출(지하철_노선_생성_요청(서울역_청량리역_구간_일호선_생성_요청()));

        // when
        ExtractableResponse<Response> 지하철_노선_삭제_응답 = 지하철_노선_삭제_요청(일호선_아이디);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철_노선_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            assertThat(지하철_노선_목록_응답에서_노선_아이디_목록_추출(지하철_노선_목록_조회_요청())).doesNotContain(일호선_아이디);
        });

    }


}
