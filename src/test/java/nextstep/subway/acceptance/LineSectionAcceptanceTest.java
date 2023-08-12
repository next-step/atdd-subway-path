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

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    Long 교대역_ID;
    Long 강남역_ID;
    Long 역삼역_ID;
    Long 선릉역_ID;
    Long 삼성역_ID;
    Long 분당선_ID;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();
        교대역_ID = 역_생성_ID_추출(지하철역_생성_요청(교대역_이름));
        강남역_ID = 역_생성_ID_추출(지하철역_생성_요청(강남역_이름));
        역삼역_ID = 역_생성_ID_추출(지하철역_생성_요청(역삼역_이름));
        선릉역_ID = 역_생성_ID_추출(지하철역_생성_요청(선릉역_이름));
        삼성역_ID = 역_생성_ID_추출(지하철역_생성_요청(삼성역_이름));
        분당선_ID = 노선_생성_ID_추출(지하철_노선_생성_요청(분당선_이름, 신분당선_색, 강남역_ID, 역삼역_ID, 거리_10));
    }

    /**
     * When 지하철 노선에 새로운 하행 종점 구간 추가를 요청 하면
     * Then 노선에 하행 종점 구간이 추가 된다
     */
    @DisplayName("지하철 노선에 하행 종점 구간을 등록")
    @Test
    void addLineLastSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(분당선_ID, 역삼역_ID, 선릉역_ID, 거리_10);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(분당선_ID);
        상태코드_확인(response, HttpStatus.OK);
        assertThat(노선의_역ID_목록_추출(response)).containsExactly(강남역_ID, 역삼역_ID, 선릉역_ID);
    }

    /**
     * When 지하철 노선에 새로운 상행 종점 구간 추가를 요청 하면
     * Then 노선에 상행 종점 구간이 추가 된다
     */
    @DisplayName("지하철 노선에 상행 종점 구간을 등록")
    @Test
    void addLineFirstSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(분당선_ID, 교대역_ID, 강남역_ID, 거리_10);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(분당선_ID);
        상태코드_확인(response, HttpStatus.OK);
        assertThat(노선의_역ID_목록_추출(response)).containsExactly(교대역_ID, 강남역_ID, 역삼역_ID);
    }

    /**
     * When 지하철 노선에 새로운 중간 구간 추가를 요청 하면
     * Then 노선 중간에 구간이 추가 된다
     */
    @DisplayName("지하철 노선 중간에 구간을 등록")
    @Test
    void addLineMiddleSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(분당선_ID, 역삼역_ID, 삼성역_ID, 거리_10);

        // when
        지하철_노선에_지하철_구간_생성_요청(분당선_ID, 선릉역_ID, 삼성역_ID, 거리_04);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(분당선_ID);
        상태코드_확인(response, HttpStatus.OK);
        assertThat(노선의_역ID_목록_추출(response)).containsExactly(강남역_ID, 역삼역_ID, 선릉역_ID, 삼성역_ID);
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
        지하철_노선에_지하철_구간_생성_요청(분당선_ID, 역삼역_ID, 선릉역_ID, 거리_10);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(분당선_ID, 선릉역_ID);
        상태코드_확인(response, HttpStatus.OK);

        // then
        assertThat(노선의_역ID_목록_추출(지하철_노선_조회_요청(분당선_ID))).containsExactly(강남역_ID, 역삼역_ID);
    }
}
