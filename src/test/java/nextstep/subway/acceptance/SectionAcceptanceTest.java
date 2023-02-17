package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.노선_조회가_성공한다;
import static nextstep.subway.acceptance.LineSteps.노선에_상행_종점역과_일치한다;
import static nextstep.subway.acceptance.LineSteps.노선에_역이_순서대로_포함되어있다;
import static nextstep.subway.acceptance.LineSteps.노선에_역이_포함되어_있지않다;
import static nextstep.subway.acceptance.LineSteps.노선에_역이_포함되어있다;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
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

@DisplayName("지하철 구간 관리 인수 테스트")
class SectionAcceptanceTest extends AcceptanceTest {

    private Long 신분당선_id;
    private Long 기존_상행_종점역_id;
    private Long 기존_하행_종점역_id;

    /**
     * Given 지하철 역과 노선 생성하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        기존_상행_종점역_id = Long으로_추출(지하철역_생성_요청(강남역.역_이름()), 식별자_아이디);
        기존_하행_종점역_id = Long으로_추출(지하철역_생성_요청(양재역.역_이름()), 식별자_아이디);

        Map<String, String> 생성_요청_데이터_생성 = 이호선.생성_요청_데이터_생성(기존_상행_종점역_id, 기존_하행_종점역_id, 강남_양재_구간.구간_거리());
        신분당선_id = Long으로_추출(지하철_노선_생성_요청(생성_요청_데이터_생성), 식별자_아이디);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 구간_등록 {

        @Nested
        @DisplayName("지하철 노선에 새로운 구간 등록을 요청하면")
        class Context_with_add_new_section {

            private Long 새로운_역_id;

            @BeforeEach
            void setUp() {
                새로운_역_id = Long으로_추출(지하철역_생성_요청(정자역.역_이름()), 식별자_아이디);
            }

            @Test
            @DisplayName("노선 조회 시 새로운 역이 추가되어 있다")
            void it_registered_section() throws Exception {
                지하철_노선에_지하철_구간_생성_요청(신분당선_id, 양재_정자_구간.요청_데이터_생성(기존_하행_종점역_id, 새로운_역_id));

                ExtractableResponse<Response> 노선_조회_결과 = 지하철_노선_조회_요청(신분당선_id);
                노선_조회가_성공한다(노선_조회_결과);
                노선에_역이_포함되어있다(노선_조회_결과, 새로운_역_id);
            }
        }

        @Nested
        @DisplayName("구간을 등록할 때 새로운 역이 상행 종점역인 경우")
        class Context_with_add_new_terminal_upstation {

            private Long 새로운_상행_종점역_id;

            @BeforeEach
            void setUp() {
                새로운_상행_종점역_id = Long으로_추출(지하철역_생성_요청(신사역.역_이름()), 식별자_아이디);
            }

            @Test
            @DisplayName("노선 목록 조회 시 역 목록은 상행 종점역을 기준으로 정렬되어 반환된다")
            void it_returns_sorted_by_terminal_upstation() throws Exception {
                지하철_노선에_지하철_구간_생성_요청(신분당선_id, 신사_강남_구간.요청_데이터_생성(새로운_상행_종점역_id, 기존_상행_종점역_id));

                ExtractableResponse<Response> 노선_조회_결과 = 지하철_노선_조회_요청(신분당선_id);
                노선_조회가_성공한다(노선_조회_결과);
                노선에_역이_순서대로_포함되어있다(노선_조회_결과, 새로운_상행_종점역_id, 기존_상행_종점역_id, 기존_하행_종점역_id);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 구간_삭제 {

        /**
         * Given 지하철 구간이 2개인 노선이 주어지고 (A-B, B-C)
         * When 지하철 구간의 하행 종점역을 삭제하면 (A-B-C -> C 삭제)
         * Then 노선 목록 조회 시 해당 역은 역 목록에서 조회되지 않는다
         */
        @Nested
        @DisplayName("지하철 구간의 하행 종점역을 삭제하면")
        class Context_with_remove_final_downstation {

            private Long 새로운_하행역_id;

            @BeforeEach
            void setUp() {
                새로운_하행역_id = Long으로_추출(지하철역_생성_요청(정자역.역_이름()), 식별자_아이디);
                지하철_노선에_지하철_구간_생성_요청(신분당선_id, 양재_정자_구간.요청_데이터_생성(기존_하행_종점역_id, 새로운_하행역_id));
            }

            @Test
            @DisplayName("노선 목록 조회 시 해당 역은 역 목록에서 조회되지 않는다")
            void it_remove_final_downstation() throws Exception {
                지하철_노선에_지하철_구간_제거_요청(신분당선_id, 새로운_하행역_id);

                ExtractableResponse<Response> 노선_조회_결과 = 지하철_노선_조회_요청(신분당선_id);
                노선_조회가_성공한다(노선_조회_결과);
                노선에_역이_포함되어_있지않다(노선_조회_결과, 새로운_하행역_id);
            }
        }


        /**
         * Given 지하철 구간이 2개인 노선이 주어지고 (A-B, B-C)
         * When 지하철 구간의 상행 종점역을 삭제하면 (A-B-C -> A 삭제)
         * Then 노선 목록 조회 시 상행 종점역은 기존 최상위 구간의 하행 종점역으로 수정된다 ("B"-C)
         */
        @Nested
        @DisplayName("지하철 구간의 상행 종점역을 삭제하면")
        class Context_with_remove_final_upstation {

            @BeforeEach
            void setUp() {
                Long 새로운_하행역_id = Long으로_추출(지하철역_생성_요청(정자역.역_이름()), 식별자_아이디);
                지하철_노선에_지하철_구간_생성_요청(신분당선_id, 양재_정자_구간.요청_데이터_생성(기존_하행_종점역_id, 새로운_하행역_id));
            }

            @Test
            @DisplayName("노선 목록 조회 시 상행 종점역은 기존 최상위 구간의 하행 종점역으로 조회된다")
            void it_changed_final_upstation() throws Exception {
                지하철_노선에_지하철_구간_제거_요청(신분당선_id, 기존_상행_종점역_id);

                ExtractableResponse<Response> 노선_조회_결과 = 지하철_노선_조회_요청(신분당선_id);
                노선에_상행_종점역과_일치한다(노선_조회_결과, 기존_하행_종점역_id);
                노선에_역이_포함되어_있지않다(노선_조회_결과, 기존_상행_종점역_id);
            }
        }


        /**
         * Given 지하철 구간이 2개인 노선이 주어지고 (A-B, B-C)
         * When 지하철 구간의 중간 역을 삭제하면 (A-B-C -> B 삭제)
         * Then 노선 목록 조회 시 역 목록은 상행 종점역을 기준으로 정렬되어 반환된다 (재배치) (A-C)
         */

        @Nested
        @DisplayName("지하철 구간의 중간 역을 삭제하면")
        class Context_with_remove_middle_station {

            private Long 새로운_하행역_id;

            @BeforeEach
            void setUp() {
                새로운_하행역_id = Long으로_추출(지하철역_생성_요청(정자역.역_이름()), 식별자_아이디);
                지하철_노선에_지하철_구간_생성_요청(신분당선_id, 양재_정자_구간.요청_데이터_생성(기존_하행_종점역_id, 새로운_하행역_id));
            }

            @Test
            @DisplayName("노선 목록 조회 시 역 목록은 재배치 되어 상행 종점역을 기준으로 정렬되어 반환된다")
            void it_relocated_station() throws Exception {
                지하철_노선에_지하철_구간_제거_요청(신분당선_id, 기존_하행_종점역_id);

                ExtractableResponse<Response> 노선_조회_결과 = 지하철_노선_조회_요청(신분당선_id);
                노선에_역이_순서대로_포함되어있다(노선_조회_결과, 기존_상행_종점역_id, 새로운_하행역_id);
                노선에_역이_포함되어_있지않다(노선_조회_결과, 기존_하행_종점역_id);
            }
        }

        /**
         * When 노선에 등록되어 있지 않은 역을 삭제하면
         * Then 409 에러 코드를 응답한다
         */


        /**
         * When 구간이 하나인 노선에서 마지막 역을 삭제하면
         * Then 409 에러 코드를 응답한다
         */
    }
}
