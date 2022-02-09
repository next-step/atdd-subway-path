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
    private Long 청계산입구역;
    private Long 정자역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        청계산입구역 = 지하철역_생성_요청("청계산입구역").jsonPath().getLong("id");
        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, 10);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 노선의 기존 구간 중간에 새로운 역을 추가 요청한다.
     * Then 지하철 구간 생성이 성공한다.
     */
    @DisplayName("지하철 노선에 구간을 등록 - 역 사이에 새로운 역을 등록하는 경우")
    @Test
    void addSectionInTheMiddle() {
        // when
        ExtractableResponse<Response> 지하철_구간_추가_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 청계산입구역, 5));

        // then
        assertThat(지하철_구간_추가_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> 지하철_노선_조회_응답 = 지하철_노선_조회_요청(신분당선);
        assertThat(지하철_노선_조회_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 청계산입구역, 양재역);
    }

    /**
     * Given 지하철 노선을 생성한다.
     * When 노선의 맨 앞에 새로운 역을 추가 요청한다. (새로운 역을 상행 종점으로 등록 요청한다.)
     * Then 지하철 구간 생성이 성공한다.
     */
    @DisplayName("지하철 노선에 구간을 등록 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionInTheFront() {
        // when
        ExtractableResponse<Response> 지하철_구간_추가_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(청계산입구역, 강남역, 10));

        // then
        assertThat(지하철_구간_추가_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> 지하철_노선_조회_응답 = 지하철_노선_조회_요청(신분당선);
        assertThat(지하철_노선_조회_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(청계산입구역, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선을 생성한다.
     * When 노선의 맨 마지막에 새로운 역을 추가 요청한다. (새로운 역을 하행 종점으로 등록 요청한다.)
     * Then 지하철 구간 생성이 성공한다.
     */
    @DisplayName("지하철 노선에 구간을 등록 - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionAtTheEnd() {
        // when
        ExtractableResponse<Response> 지하철_구간_추가_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 청계산입구역, 10));

        // then
        assertThat(지하철_구간_추가_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> 지하철_노선_조회_응답 = 지하철_노선_조회_요청(신분당선);
        assertThat(지하철_노선_조회_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 청계산입구역);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
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
