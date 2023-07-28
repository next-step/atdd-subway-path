package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
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

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
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
     * When 지하철 노선에서 기존 구간 사이에 새로운 구간을 추가하면
     * Then 구간은 2개로 나뉘고, 총 길이는 원래 구간의 길이와 동일하다.
     */
    @Test
    @DisplayName("지하철 노선 구간 추가 - 역 사이에 새로운 역 추가 (상행역 기준)")
    void addLineSectionBetween_상행역_기준() {

        // when
        Long 강남_양재_중간역 = 지하철역_생성_요청("강남_양재_중간역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 강남_양재_중간역, 3L));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(강남역, 강남_양재_중간역, 양재역);
    }

    /**
     * When 지하철 노선에서 기존 구간 사이에 새로운 구간을 추가하면
     * Then 구간은 2개로 나뉘고, 역 목록 사이에 새로운 역이 추가된다.
     */
    @Test
    @DisplayName("지하철 노선 구간 추가 - 역 사이에 새로운 역 추가(하행역 기준)")
    void addLineSectionBetween_하행역_기준() {
        // when
        Long 강남_양재_중간역 = 지하철역_생성_요청("강남_양재_중간역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남_양재_중간역, 양재역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(강남역, 강남_양재_중간역, 양재역);
    }

    /**
     * When 지하철 노선에서 상행 종점역을 새로 추가하면
     * Then Then 구간은 2개로 나뉘고, 역 목록 사이에 새로운 역이 추가된다.
     */
    @Test
    @DisplayName("지하철 노선 구간 추가 - 신규 상행 종점역 추가")
    void addLineSection_신규_상행_종점역() {

        // when
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(신논현역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선에서 상행 종점역을 새로 추가하면
     * Then Then 구간은 2개로 나뉘고, 역 목록 사이에 새로운 역이 추가된다.
     */
    @Test
    @DisplayName("지하철 노선 구간 추가 - 신규 하행 종점역 추가")
    void addLineSection_신규_하행_종점역() {
        // when
        Long 양재시민의숲역 = 지하철역_생성_요청("양재시민의숲역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재시민의숲역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(강남역, 양재역, 양재시민의숲역);
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
        return this.createSectionCreateParams(upStationId, downStationId, 6L);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
