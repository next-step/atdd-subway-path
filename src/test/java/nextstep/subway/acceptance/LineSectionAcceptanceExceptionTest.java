package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 예외")
class LineSectionAcceptanceExceptionTest extends AcceptanceTest {
    static final int 강남_양재_거리 = 7;
    private Long 신분당선;
    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        노선_생성_파라미터 노선_생성_파라미터 = new 노선_생성_파라미터(강남역, 양재역, 강남_양재_거리);
        신분당선 = 지하철_노선_생성_요청(노선_생성_파라미터).jsonPath().getLong("id");
    }

    /**
     * When 역 사이에 새로운 역 등록 기존 역의 사이의 길이와 같은 구간을 추가하면
     * Then 구간 추가가 실패한다
     */
    @DisplayName("역 사이에 새로운 역 등록시 추가하려는 사이 구간의 길이보다 새로운 구간의 길이가 작아야 한다")
    @Test
    void addSectionExceptionWhenNewSectionDistanceIsEqualToExistsSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, new 구간_생성_파라미터(강남역, 정자역, 강남_양재_거리));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상행역과 하행역이 이미 노선에 모두 등록된 구간을 등록하면
     * Then 구간 추가가 실패한다.
     */
    @DisplayName("상행역과 하행역이 모두 노선에 등록되어 있는 경우의 구간 등록")
    @Test
    void addSectionExceptionWhenStationsAreExist() {
        // when
        ExtractableResponse<Response> response =
                지하철_노선에_지하철_구간_생성_요청(
                        신분당선,
                        new 구간_생성_파라미터(강남역, 양재역, 강남_양재_거리 - 1));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상행역과 하행역 모두 노선에 존재하지 않는 구간을 등록하면
     * Then 구간등록이 실패한다.
     */
    @DisplayName("상행역과 하행역이 모두 노선에 없는 경우의 구간 등록")
    @Test
    void addSectionExceptionWhenAtLeastOneStationNotExists() {
        // given
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response =
                지하철_노선에_지하철_구간_생성_요청(
                        신분당선,
                        new 구간_생성_파라미터(판교역, 정자역, 강남_양재_거리 - 1));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
