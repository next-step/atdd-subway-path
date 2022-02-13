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
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - (A-C + A-B => A-B-C)")
    @Test
    void addLineSectionCase1() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        System.out.println(response.jsonPath().getList("stations.id", Long.class));
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 마지막 역을 제거")
    @Test
    void removeLineSectionCase1() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 3));

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 첫번째 역 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("노선에 첫번째 역을 제거")
    @Test
    void removeLineSectionCase2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);
        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(신분당선);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
    }

    /**
     * Given (상행)A역-(하행)B역, (상행)B역-(하행)C역 구간이 있을때
     * When  중간 역인 B역을 제거하면
     * Then  노선에 포함된 구간은 (상행)A역-(하행)C역으로 변경 된다.
     * And   A역과 C역과의 거리도 다음 계산식의 결과로 변경된다. (A역과 C역의 거리 = A역과 B역의 거리 + B역과 C역의 거리)
     */
    @DisplayName("노선에 중간 역을 제거")
    @Test
    void removeLineSectionCase3() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);
        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(신분당선);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    /**
     * When  노선에 등록되어 있지 않은 지하철 역 제거를 요청할때
     * Then  요청은 실패한다.
     */
    @DisplayName("노선에 등록되어 있지 않은 지하철역 제거 요청")
    @Test
    void removeLineSectionFailCase1() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 수지구청역 = 지하철역_생성_요청("수지구청역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 수지구청역);
        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(신분당선);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 노선에 구간이 1개만 있을때
     * When  구간 삭제를 요청하면
     * Then  요청은 실패한다.
     */
    @DisplayName("구간이 1개만 있을때 지하철 역 삭제 요청")
    @Test
    void removeLineSectionFailCase3() {
        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
        return createSectionCreateParams(upStationId, downStationId, 6);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}