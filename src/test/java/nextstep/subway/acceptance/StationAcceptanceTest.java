package nextstep.subway.acceptance;

import static nextstep.subway.support.fixture.StationFixture.강남역_이름;
import static nextstep.subway.support.fixture.StationFixture.강남역_생성_요청;
import static nextstep.subway.support.fixture.StationFixture.교대역_이름;
import static nextstep.subway.support.fixture.StationFixture.교대역_생성_요청;
import static nextstep.subway.support.step.StationSteps.지하철_역_삭제_요청;
import static nextstep.subway.support.step.StationSteps.지하철_역_생성_요청;
import static nextstep.subway.support.step.StationSteps.지하철역_목록_응답에서_역_이름_목록_추출;
import static nextstep.subway.support.step.StationSteps.지하철역_목록_조회_요청;
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

@DisplayName("지하철역 관련 기능")
@AcceptanceTest
class StationAcceptanceTest {


    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철_역_생성_요청(강남역_생성_요청());

        // then
        assertThat(강남역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(지하철역_목록_응답에서_역_이름_목록_추출(지하철역_목록_조회_요청())).containsAnyOf(강남역_이름);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStationList() {
        // given
        지하철_역_생성_요청(강남역_생성_요청());
        지하철_역_생성_요청(교대역_생성_요청());

        // when
        ExtractableResponse<Response> 지하철역_목록_조회_응답 = 지하철역_목록_조회_요청();

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철역_목록_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(지하철역_목록_조회_응답.body().as(List.class)).hasSize(2);
            assertThat(지하철역_목록_응답에서_역_이름_목록_추출(지하철역_목록_조회_응답)).containsAnyOf(강남역_이름, 교대역_이름);
        });

    }


    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        // given
        지하철_역_생성_요청(교대역_생성_요청());
        Long 강남역_아이디 = 지하철역_응답에서_역_아이디_추출(지하철_역_생성_요청(강남역_생성_요청()));

        // when
        ExtractableResponse<Response> 지하철역_삭제_응답 = 지하철_역_삭제_요청(강남역_아이디);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(지하철역_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            assertThat(지하철역_목록_응답에서_역_이름_목록_추출(지하철역_목록_조회_요청())).doesNotContain(강남역_이름);
        });

    }


}
