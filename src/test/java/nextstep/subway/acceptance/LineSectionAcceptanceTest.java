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

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    @DisplayName("상행역을 기준으로 역 사이에 새로운 역으로 구간 등록")
    @Test
    void addSection() {
        // when
        // 신분당선: 강남역-양재역

        // 신분당선: 상행종점역-강남역-양재역
        final Long 상행종점역 = 지하철역_생성_요청("상행종점역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(상행종점역, 강남역, 6));

        // 신분당선: 상행종점역-상행역-강남역-양재역
        final Long 상행역 = 지하철역_생성_요청("상행역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(상행역, 강남역, 5));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(상행종점역, 상행역, 강남역, 양재역);
    }

    @DisplayName("하행역을 기준으로 역 사이에 새로운 역을 등록")
    @Test
    void addSection2() {
        // when
        // 신분당선: 강남역-양재역

        // 신분당선: 강남역-양재역-하행종점역
        final Long 하행종점역 = 지하철역_생성_요청("하행종점역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 하행종점역, 6));

        // 신분당선: 강남역-양재역-하행역-하행종점역
        final Long 하행역 = 지하철역_생성_요청("하행역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(하행역, 하행종점역, 5));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 하행역, 하행종점역);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void addSection3() {
        // when
        // 신분당선: 강남역-양재역

        // 신분당선: 상행종점역-강남역-양재역
        final Long 상행종점역 = 지하철역_생성_요청("상행종점역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(상행종점역, 강남역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(상행종점역, 강남역, 양재역);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void addSection4() {
        // when
        // 신분당선: 강남역-양재역

        // 신분당선: 강남역-양재역-하행종점역
        final Long 하행종점역 = 지하철역_생성_요청("하행종점역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 하행종점역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 하행종점역);
    }

    @DisplayName("새로운 구간 등록 실패 - distance 부족 ")
    @Test
    void addSection5() {
        // when
        // 신분당선: 강남역-양재역

        // 신분당선: 강남역-양재역-하행종점역
        final Long 하행종점역 = 지하철역_생성_요청("하행종점역").jsonPath().getLong("id");
        final int sameDistance = 6;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 하행종점역, sameDistance));

        // 신분당선: 강남역-양재역-하행역-하행종점역
        final Long 하행역 = 지하철역_생성_요청("하행역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(하행역, 하행종점역, sameDistance));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 하행종점역);
    }

    @DisplayName("새로운 구간 등록 실패 - 같은 역 등록 불가 ")
    @Test
    void addSection6() {
        // when
        // 신분당선: 강남역-양재역

        // 신분당선: 강남역-양재역-하행종점역
        final Long 하행종점역 = 지하철역_생성_요청("하행종점역").jsonPath().getLong("id");
        final int sameDistance = 6;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 하행종점역, sameDistance));

        // 신분당선: 강남역-양재역-하행종점역(X)-하행종점역
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 하행종점역, sameDistance));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 하행종점역);
    }

    @DisplayName("새로운 구간 등록 실패 - 라인에 포함되지 않은 역 등록 불가")
    @Test
    void addSection7() {
        // when
        // 신분당선: 강남역-양재역
        final Long 새로운역 = 지하철역_생성_요청("새로운역").jsonPath().getLong("id");
        final Long 또다른역 = 지하철역_생성_요청("또다른역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(새로운역, 또다른역, 1));

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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거 - 마지막역 제거, 이전 역이 하행 종점")
    @Test
    void removeLineSection() {
        // given
        // 신분당선 : 강남역 - 양재역 - 정자역
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        // 신분당선 : 강남역 - 양재역
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    @DisplayName("지하철 노선에 구간을 제거 - 상행 종점 제거, 다음역이 상행 종점")
    @Test
    void removeLineSection2() {
        // given
        // 신분당선 : 강남역 - 양재역 - 정자역
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        // 신분당선 : 강남역 - 양재역
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
    }

    @DisplayName("지하철 노선에 구간을 제거 - 중간역 제거, 순서 재배치")
    @Test
    void removeLineSection3() {
        // given
        // 신분당선 : 강남역 - 양재역 - 정자역
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        // 신분당선 : 강남역 - 정자역
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    // todo error case 추가
}
