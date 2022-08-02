package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 삼성역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역));

        삼성역 = 지하철역_생성_요청("삼성역").jsonPath().getLong("id");
    }

    @Nested
    class 구간_등록 {

        @Nested
        class 성공 {
            /**
             * when 새로운 역을 상행 종점으로 구간 등록하면
             * then 새로운 역이 상행 종점으로 등록된다.
             */
            @DisplayName("새로운 역을 상행 종점으로 등록")
            @Test
            void 상행_종점_등록 () {
                // when
                ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(삼성역, 강남역));

                // then
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(삼성역, 강남역, 양재역);
            }

            /**
             * when 새로운 역을 하행 종점으로 구간 등록하면
             * then 새로운 역이 하행 종점으로 등록된다.
             */
            @DisplayName("새로운 역을 하행 종점으로 등록")
            @Test
            void 하행_종점_등록 () {
                // when
                ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 삼성역));

                // then
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 삼성역);
            }

            /**
             * when 기존 구간에 새로운 역 등록하면
             * then 새로운 역이 등록된다.
             */
            @DisplayName("역 사이에 새로운 역 등록")
            @Test
            void 역사이_새로운역_등록 () {
                // when
                ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 삼성역));

                // then
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 삼성역, 양재역);
            }
        }

        @Nested
        class 실패 {
            /**
             * when 역 사이에 새로운 역 등록할 경우
             * when 기존 역 사이 길이보다 크거나 같은 구간을 등록하면
             * then 예외가 발생한다.
             */
            @DisplayName("기존 역 사이 길이보다 크거나 같으면 예외 발생")
            @Test
            void 구간_길이_예외 () {
                // when
                ExtractableResponse<Response> 구간_생성 =
                        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 삼성역, 100));

                // then
                예외_발생(구간_생성);
            }

            /**
             * when 이미 모두 등록되어있는 상행역과 하행역을 등록하면
             * then 예외가 발생한다.
             */
            @DisplayName("상행역과 하행역 모두 등록되어있으면 예외 발생")
            @Test
            void 상행역_하행역_모두_등록_예외 () {
                // when
                ExtractableResponse<Response> 구간_생성 =
                        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역));

                // then
                예외_발생(구간_생성);
            }

            /**
             * given 역을 등록하고
             * when 모두 등록되어있지 않은 상행역과 하행역을 등록하면
             * then 예외가 발생한다.
             */
            @DisplayName("상행역과 하행역 모두 등록되어있지 않으면 예외 발생")
            @Test
            void 상행역_하행역_모두_미등록_예외 () {
                // given
                Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

                // when
                ExtractableResponse<Response> 구간_생성 =
                        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(삼성역, 판교역));

                // then
                예외_발생(구간_생성);
            }
        }

    }

    @Nested
    class 구간_제거 {

        @Nested
        class 성공 {
            /**
             * given 노선에 구간을 등록하고
             * when 중간에 있는 역을 제거하면
             * then 위치와 상관 없이 제거된다.
             */
            @DisplayName("중간에 있는 역 제거")
            @Test
            void 중간_역_제거 () {
                // given
                지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 삼성역));

                // when
                ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

                // then
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 삼성역);
            }

            /**
             * given 노선에 구간을 등록하고
             * when 상행 종점을 제거하면
             * then 제거된다.
             */
            @DisplayName("상행 종점 제거")
            @Test
            void 상행_종점_제거 () {

            }

            /**
             * given 노선에 구간을 등록하고
             * when 하행 종점을 제거하면
             * then 제거된다.
             */
            @DisplayName("하행 종점 제거")
            @Test
            void 하행_종점_제거 () {

            }
        }

        @Nested
        class 실패 {
            /**
             * when 구간이 하나인 노선에서 역을 제거하면
             * then 예외가 발생한다.
             */
            @DisplayName("구간이 하나인 노선의 역을 제거하면 예외 발생")
            @Test
            void 마지막_구간_제거_예외 () {
                // when
                ExtractableResponse<Response> 구간_제거 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

                // then
                예외_발생(구간_제거);
            }

            /**
             * when 등록되어있지 않은 구간을 제거하면
             * then 예외가 발생한다.
             */
            @DisplayName("등록되지 않은 구간을 제거하면 예외 발생")
            @Test
            void 미등록_구간_제거_예외 () {
                // when
                ExtractableResponse<Response> 구간_제거 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 삼성역);

                // then
                예외_발생(구간_제거);
            }

            /**
             * when 존재하지 않은 역을 제거하면
             * then 예외가 발생한다.
             */
            @DisplayName("존재하지 않은 역을 제거하면 예외 발생")
            @Test
            void 미등록_역_제거_예외 () {
                // given
                Long 판교역 = 123L;

                // when
                ExtractableResponse<Response> 구간_제거 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 판교역);

                // then
                예외_발생(구간_제거);
            }
        }

    }

    private void 예외_발생(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", 6 + "");
        return params;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
