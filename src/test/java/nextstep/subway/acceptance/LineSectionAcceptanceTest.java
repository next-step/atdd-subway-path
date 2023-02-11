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
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
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
         * When 지하철 노선 기존 구간 맨 앞에 새로운 구간 추가 요청 시
         * Then 노선에 새로운 구간이 추가된다
         */
        @DisplayName("지하철 노선 기존 구간 맨 앞에 새로운 구간 추가 요청 시 새로운 구간이 추가된다")
        @Test
        void 지하철_노선_기존_구간_맨_앞에_새로운_구간_추가_요청_시_새로운_구간이_추가된다() {
            // when
            Long 신사역 = 지하철역_생성_요청("신사역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신사역, 강남역));

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신사역, 강남역, 양재역);
        }

        /**
         * When 지하철 노선 기존 구간 맨 뒤에 새로운 구간 추가 요청 시
         * Then 노선에 새로운 구간이 추가된다
         */
        @DisplayName("지하철 노선 기존 구간 맨 뒤에 새로운 구간 추가 요청 시 새로운 구간이 추가된다")
        @Test
        void 지하철_노선_기존_구간_맨_뒤에_새로운_구간_추가_요청_시_새로운_구간이_추가된다() {
            // when
            Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 판교역));

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly( 강남역, 양재역, 판교역);
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
