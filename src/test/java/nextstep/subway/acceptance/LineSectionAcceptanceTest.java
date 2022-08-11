package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
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
     * Given 지하철 노선 생성 후 하나의 구간(A-C)을 등록하고
     * When 노선의 기존 구간에 A-B 구간을 새로 추가하면
     * Then A-B, A-C두 개의 구간이 노선이 만들어진다.
     */
    @DisplayName("지하철 노선에 구간 추가(A역-C역 사이에 B역을 추가)")
    @Test
    void addLineSectionBetween() {
        // 강남역 - 양재역 ---> 강남역 - 수원역 - 양재역
        // given
        Long 수원역 = 지하철역_생성_요청("수원역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 수원역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 수원역, 양재역);
    }

    /**
     * Given 지하철 노선 생성 후 하나의 구간(B-C)을 등록하고
     * When 노선의 기존 구간에 A-B 구간을 새로 등록하면
     * Then A-B, B-C 두 개의 구간이 만들어진다.
     */
    @DisplayName("지하철 노선에 구간 추가(새로운 역을 상행 종점으로 등록)")
    @Test
    void addLineSectionWithNewLastUpStation() {
        // 강남역 - 양재역 ===> 신논현역 - 강남역 - 양재역 ===> 논현역 - 신논현역 - 강남역 - 양재역
        // given
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        Long 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 신논현역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(논현역, 신논현역, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선 생성 후 하나의 구간(A-B)을 등록하고
     * When 노선의 기존 구간에 B-C 구간을 새로 등록하면
     * Then A-B, B-C 두 개의 구간이 만들어진다.
     */
    @DisplayName("지하철 노선에 구간 추가(새로운 역을 하행 종점으로 등록)")
    @Test
    void addLineSectionWithNewLastDownStation() {
        // 강남역 - 양재역 ===> 강남역 - 양재역 - 수원역 ===> 강남역 - 양재역 - 수원역 - 노들역
        // given
        Long 수원역 = 지하철역_생성_요청("수원역").jsonPath().getLong("id");
        Long 노들역 = 지하철역_생성_요청("노들역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 수원역));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(수원역, 노들역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 수원역, 노들역);
    }

    /**
     * Given 지하철 노선 생성 후 길이가 10인 하나의 구간(A-C)을 등록하고
     * When 노선의 기존 구간에 길이가 15인 A-B 구간을 새로 등록하면
     * Then 거리가 더 긴 구간을 중간에 삽입하려는 잘못된 요청으로 인해 에러가 발생한다
     */
    @DisplayName("지하철 노선에 구간 추가 실패(매칭되는 기존 구간보다 거리가 더 긺)")
    @Test
    void addLineSectionFailLongerDistance() {
        // [실패] 강남역 - 양재역 -> 강남역 - 수원역 - 양재역
        // given
        Long 수원역 = 지하철역_생성_요청("수원역").jsonPath().getLong("id");
        final int 강남역_수원역_거리 = 15;

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 수원역, 강남역_수원역_거리));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

        ExtractableResponse<Response> getLineResponse = 지하철_노선_조회_요청(신분당선);
        assertThat(getLineResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선 생성 후 하나의 구간(A-B)을 등록하고
     * When 노선의 기존 구간과 동일한 A-B 구간을 다시 등록하려고 하면
     * Then 똑같은 구간을 다시 등록하려는 잘못된 요청으로 인해 에러가 발생한다
     */
    @DisplayName("지하철 노선에 구간 추가 실패(이미 등록되어 있는 구간)")
    @Test
    void addSectionFailAlreadyRegistered() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

        ExtractableResponse<Response> getLineResponse = 지하철_노선_조회_요청(신분당선);
        assertThat(getLineResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선 생성 후 하나의 구간(A-B)을 등록하고
     * When 구간 C-D를 노선에 새롭게 등록하려고 하면
     * Then 기존 구간의 상행역(A) 혹은 하행역(B) 어디에도 이어붙일 수 잘못된 요청으로 인해 에러가 발생한다
     */
    @DisplayName("지하철 노선에 구간 추가 실패(상행역 하행역 둘 중 하나도 포함되어 있지 않음)")
    @Test
    void addSectionFailNoneMatchingUpAndDownStations() {
        // given
        Long 구로역 = 지하철역_생성_요청("구로역").jsonPath().getLong("id");
        Long 신도림역 = 지하철역_생성_요청("신도림역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(구로역, 신도림역));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

        ExtractableResponse<Response> getLineResponse = 지하철_노선_조회_요청(신분당선);
        assertThat(getLineResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

}
