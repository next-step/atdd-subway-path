package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.SectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {

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

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    @Nested
    @DisplayName("지하철 노선에 구간 등록")
    class 지하철_노선에_구간_생성 {

        /**
         * When 지하철 노선에 새로운 구간 추가를 요청 하면
         * Then 노선에 새로운 구간이 추가된다
         */
        @DisplayName("지하철 노선에 새로운 구간을 등록 할 수 있다")
        @Test
        void addLineSection() {
            // when
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getString("id")).isEqualTo("1");
            assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
            assertThat(response.jsonPath().getString("color")).isEqualTo("bg-red-600");
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
            assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "양재역", "정자역");
        }

        /**
         * When 기존 지하철 노선 구간 상행역을 기준으로 새로운 구간 추가를 요청 하면
         * Then 기존 구간에 새로운 구간이 추가 되며
         * Then 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정 된다
         */
        @DisplayName("기존 구간의 상행역을 기준으로 새로운 구간을 추가")
        @Test
        void 기존_구간의_상행역을_기준으로_새로운_구간을_추가_된다() {
            // when
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 4L));

            // then
            ExtractableResponse<Response> lineResponse = 지하철_노선_구간_조회_요청(신분당선);
            노선에_새로운_구간이_추가되며_길이가_재_정의_된다(정자역, lineResponse);
        }

        /**
         * When 새로운 역을 상행 종점으로 등록할 경우
         * Then 새로운 구간이 추가된다
         */
        @DisplayName("새로운 역을 상행 종점으로 등록할 경우 새로운 구간이 추가 된다")
        @Test
        void 새로운_역을_상행_종점으로_등록할_경우_새로운_구간이_추가_된다() {
            // when
            Long 신사역 = 지하철역_생성_요청("신사역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신사역, 강남역));

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신사역, 강남역, 양재역);
        }

        /**
         * When 새로운 역을 하행 종점으로 등록할 경우
         * Then 새로운 구간이 추가된다
         */
        @DisplayName("새로운 역을 하행 종점으로 등록할 경우 새로운 구간이 추가된다")
        @Test
        void 새로운_역을_하행_종점으로_등록할_경우_새로운_구간이_추가된다() {
            // when
            Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 판교역));

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 판교역);
        }

        /**
         * When 노선의 기존 구간들과 상행역과 하행역에 이미 등록 되어있는 경우
         * Then 기존 구간에 추가가 안된다
         */
        @DisplayName("노선의 기존 구간들과 상행역과 하행역에 이미 등록 되어있는 경우 기존 구간에 추가가 안된다")
        @Test
        void 노선의_기존_구간들과_상행역과_하행역에_이미_등록_되어있는_경우_기존_구간에_추가가_안된다() {
            // when
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 4L));

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         * When 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면
         * Then 구간 등록을 할 수 없다
         */
        @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 구간 등록을 할 수 없다")
        @Test
        void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_구간_등록을_할_수_없다() {
            // when
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 10L));

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         * When 상행역과 하행역 둘 중 하나도 포함되어있지 않으면
         * Then 구간 등록을 할 수 없다
         */
        @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 구간 등록을 할 수 없다")
        @Test
        void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_구간_등록을_할_수_없다() {
            // when
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            Long 수지구청 = 지하철역_생성_요청("수지구청").jsonPath().getLong("id");
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 수지구청, 10L));

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        private void 노선에_새로운_구간이_추가되며_길이가_재_정의_된다(Long 정자역, ExtractableResponse<Response> lineResponse) {
            assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
            List<SectionResponse> sections = lineResponse.jsonPath().getList("sections", SectionResponse.class);
            assertThat(sections.get(0).getUpStation().getId()).isEqualTo(정자역);
            assertThat(sections.get(0).getDownStation().getId()).isEqualTo(양재역);
            assertThat(sections.get(0).getDistance()).isEqualTo(6);
            assertThat(sections.get(1).getUpStation().getId()).isEqualTo(강남역);
            assertThat(sections.get(1).getDownStation().getId()).isEqualTo(정자역);
            assertThat(sections.get(1).getDistance()).isEqualTo(4);
        }
    }

    @Nested
    @DisplayName("구간 삭제")
    class 지하철_노선에_구간_삭제 {

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
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

            // when
            지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
        }

        /**
         * Given 지하철 노선에 새로운 구간 추가를 요청 하고
         * When 지하철 노선의 어느 구간이든 제거를 요청 하면
         * Then 노선에 구간이 제거된다
         */
        @DisplayName("지하철 노선에 어느 구간이든 제거 할 수 있다")
        @Test
        void 지하철_노선에_어느_구간이든_제거_할_수_있다() {
            // given
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

            // when
            지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
        }

        /**
         * Given 지하철 노선에 새로운 구간 추가를 요청 하고
         * When 종점을 제거 요청 시
         * Then 다음으로 오던 역이 종점이 된다
         */
        @DisplayName("지하철 노선에 종점을 제거 요청 시 다음으로 오던 역이 종점이 된다")
        @Test
        void 지하철_노선에_종점을_제거_요청_시_다음으로_오던_역이_종점이_된다() {
            // given
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

            // when
            지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
        }

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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
