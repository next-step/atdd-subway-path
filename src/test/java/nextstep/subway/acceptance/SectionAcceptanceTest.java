package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.SectionAcceptanceAssert.기존_구간_사이에_신규_구간을_추가_검증;
import static nextstep.subway.acceptance.SectionAcceptanceAssert.노선의_상행_종점으로_신규_구간을_추가_검증;
import static nextstep.subway.acceptance.SectionAcceptanceAssert.지하철_노선에_구간을_등록_검증;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 선릉역;
    private Long 정자역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        선릉역 = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");
        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    @DisplayName("노선 구간 추가 관련 기능")
    @Nested
    class AddLineSectionTest {
        /**
         * When 지하철 노선에 새로운 구간 추가를 요청 하면
         * Then 노선에 새로운 구간이 추가된다
         */
        @DisplayName("지하철 노선에 구간을 등록")
        @Test
        void addLineSection() {
            // when
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

            // then
            지하철_노선에_구간을_등록_검증(신분당선, List.of(강남역, 양재역, 정자역));
        }

        /**
         * When 기존 구간 사이에 신규 구간을 추가를 요청 하면
         * Then 노선의 기존 구간 사이에 구간이 추가된다.
         */
        @DisplayName("기존 구간 사이에 신규 구간을 추가한다.")
        @Test
        void addSectionBetweenExistingSection() {
            // when
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 선릉역, 3));

            // then
            기존_구간_사이에_신규_구간을_추가_검증(신분당선, List.of(강남역, 선릉역, 양재역));
        }

        /**
         * When 기존 구간 사이에 신규 구간을 추가를 요청했을 때 추가를 요청한 신규 구간이 기존 역 사이 길이보다 크거나 같으면
         * Then 에러 처리한다.
         */
        @DisplayName("기존 구간 사이에 신규 구간을 추가시 신규 구간이 역과 역 사이 길이보다 크거나 같으면 에러 처리한다.")
        @ParameterizedTest(name = "sectionDistance : lineDistance + {0}")
        @ValueSource(ints = {10, 11, 15})
        void addSectionDistanceMoreThanExistingSectionDistance(int distance) {
            // when
            ExtractableResponse<Response> response
                    = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 선릉역, distance));

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * When 신규 구간을 상행 종점으로 추가를 요청 하면
         * Then 노선의 상행 종점으로 구간이 추가된다.
         */
        @DisplayName("노선의 상행 종점으로 신규 구간을 추가한다.")
        @Test
        void addLineSectionUpStation() {
            // when
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(선릉역, 강남역, 6));

            // then
            노선의_상행_종점으로_신규_구간을_추가_검증(신분당선, List.of(선릉역, 강남역, 양재역));
        }

        /**
         * When 신규 구간을 하행 종점으로 추가를 요청 하면
         * Then 노선의 하행 종점으로 구간이 추가된다.
         */
        @DisplayName("노선의 하행 종점으로 신규 구간을 추가한다.")
        @Test
        void addLineSectionDownStation() {
            // when
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 선릉역, 6));

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 선릉역);
        }

        /**
         * When 신규 구간을 추가를 요청했을 때 상행역과 하행역이 이미 노선에 모두 등록되어 있다면
         * Then 에러 처리한다.
         */
        @DisplayName("신규 구간 추가시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 에러 처리한다.")
        @Test
        void addLineSectionAlreadyAddedInLine() {
            // when
            ExtractableResponse<Response> response
                    = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 4));

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * When 신규 구간을 추가를 요청했을 때 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면
         * Then 에러 처리한다.
         */
        @DisplayName("신규 구간 추가시 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 에러 처리한다.")
        @Test
        void addLineSectionNonIncludeInLine() {
            // when
            ExtractableResponse<Response> response
                    = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(선릉역, 정자역, 4));

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 노선 조회를 요청 하면
     * Then 상행 종점역부터 하행 종점역 순서로 역 목록을 조회한다.
     */
    @DisplayName("노선 조회시 상행 종점역부터 하행 종점역 순으로 역 목록을 조회한다.")
    @Test
    void showStationsOrderByUpStationToDownStation() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 6));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
