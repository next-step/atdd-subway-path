package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.fixture.FieldFixture.노선_내_역_아이디;
import static nextstep.subway.fixture.FieldFixture.식별자_아이디;
import static nextstep.subway.fixture.LineFixture.이호선;
import static nextstep.subway.fixture.SectionFixture.강남_양재_구간;
import static nextstep.subway.fixture.SectionFixture.신사_강남_구간;
import static nextstep.subway.fixture.SectionFixture.양재_정자_구간;
import static nextstep.subway.fixture.StationFixture.강남역;
import static nextstep.subway.fixture.StationFixture.신사역;
import static nextstep.subway.fixture.StationFixture.양재역;
import static nextstep.subway.fixture.StationFixture.정자역;
import static nextstep.subway.utils.JsonPathUtil.Long으로_추출;
import static nextstep.subway.utils.JsonPathUtil.리스트로_추출;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 인수 테스트")
class SectionAcceptanceTest extends AcceptanceTest {

    private Long 신분당선_id;
    private Long 강남역_id;
    private Long 양재역_id;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역_id = Long으로_추출(지하철역_생성_요청(강남역.역_이름()), 식별자_아이디);
        양재역_id = Long으로_추출(지하철역_생성_요청(양재역.역_이름()), 식별자_아이디);

        Map<String, String> 생성_요청_데이터_생성 = 이호선.생성_요청_데이터_생성(강남역_id, 양재역_id, 강남_양재_구간.노선_간_거리());
        신분당선_id = Long으로_추출(지하철_노선_생성_요청(생성_요청_데이터_생성), 식별자_아이디);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 구간_등록 {

        @Nested
        @DisplayName("지하철 노선에 새로운 구간 추가를 요청하면")
        class Context_with_add_new_section {

            private Long 새로운_역_id;

            @BeforeEach
            void setUp() {
                // when
                새로운_역_id = Long으로_추출(지하철역_생성_요청(정자역.역_이름()), 식별자_아이디);
                지하철_노선에_지하철_구간_생성_요청(신분당선_id, 양재_정자_구간.요청_데이터_생성(양재역_id, 새로운_역_id));
            }

            @Test
            @DisplayName("노선에 새로운 구간이 추가된다")
            void it_registered_section() throws Exception {
                // then
                ExtractableResponse<Response> 노선_조회_결과 = 지하철_노선_조회_요청(신분당선_id);
                assertThat(노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(리스트로_추출(노선_조회_결과, 노선_내_역_아이디, Long.class))
                        .hasSize(3)
                        .containsAnyOf(새로운_역_id);
            }
        }

        @Nested
        @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
        class Context_with_add_new_terminal_upstation {

            private Long 새로운_상행_종점역_id;

            @BeforeEach
            void setUp() {
                // when
                새로운_상행_종점역_id = Long으로_추출(지하철역_생성_요청(신사역.역_이름()), 식별자_아이디);
                지하철_노선에_지하철_구간_생성_요청(신분당선_id, 신사_강남_구간.요청_데이터_생성(새로운_상행_종점역_id, 강남역_id));
            }

            @Test
            @DisplayName("노선 목록 조회 시 역 목록은 상행 종점역을 기준으로 정렬되어 반환된다")
            void it_returns_sorted_by_terminal_upstation() throws Exception {
                // then
                ExtractableResponse<Response> 노선_조회_결과 = 지하철_노선_조회_요청(신분당선_id);
                assertThat(노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(리스트로_추출(노선_조회_결과, 노선_내_역_아이디, Long.class))
                        .hasSize(3)
                        .containsExactly(새로운_상행_종점역_id, 강남역_id, 양재역_id);
            }
        }
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
        Long 정자역_id = Long으로_추출(지하철역_생성_요청(정자역.역_이름()), 식별자_아이디);
        지하철_노선에_지하철_구간_생성_요청(신분당선_id, 양재_정자_구간.요청_데이터_생성(양재역_id, 정자역_id));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선_id, 정자역_id);

        // then
        ExtractableResponse<Response> 노선_조회_결과 = 지하철_노선_조회_요청(신분당선_id);
        assertThat(노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(리스트로_추출(노선_조회_결과, 노선_내_역_아이디, Long.class)).containsExactly(강남역_id, 양재역_id);
    }
}
