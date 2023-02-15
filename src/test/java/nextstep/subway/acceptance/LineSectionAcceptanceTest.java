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

    private static final int CREATE_LINE_DISTANCE = 10;
    private static final int ADD_SECTION_DISTANCE = 5;

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

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, CREATE_LINE_DISTANCE);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간의 상행역과 기존 구간의 상행역이 같은 경우 구간을 추가하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 역 사이에 새로운 역을 등록할 경우 - 새로운 구간의 상행역과 기존 구간의 상행역이 같은 경우")
    @Test
    void addLineSection_1() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, ADD_SECTION_DISTANCE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 지하철 노선에 새로운 구간의 하행역과 기존 구간의 하행역이 같은 경우 구간을 추가하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 역 사이에 새로운 역을 등록할 경우 - 새로운 구간의 하행역과 기존 구간의 하행역이 같은 경우")
    @Test
    void addLineSection_2() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, ADD_SECTION_DISTANCE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 지하철 노선에 새로운 역을 상행 종점으로 등록할 경우 구간을 추가하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addLineSection_3() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역,ADD_SECTION_DISTANCE ));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선에 새로운 역을 하행 종점으로 등록할 경우 구간을 추가하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addLineSection_4() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, ADD_SECTION_DISTANCE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선에 기존 구간 길이보다 크거나 같은 새로운 구간을 추가하면
     * Then 노선이 그대로이다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 역 사이에 새로운 역을 등록하는 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addLineSection_5() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, CREATE_LINE_DISTANCE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 지하철 노선에 상행역과 하행역이 모두 존재하는 새로운 구간을 추가하면
     * Then 노선이 그대로이다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addLineSection_6() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, ADD_SECTION_DISTANCE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 지하철 노선에 상행역과 하행역이 모두 포함되지 않는 새로운 구간을 추가하면
     * Then 노선이 그대로이다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addLineSection_7() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 선릉역 = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 선릉역, ADD_SECTION_DISTANCE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 종점 하행역 제거 요청을 하면
     * Then 다음으로 오던 역이 종점이 된다
     */
    @DisplayName("지하철 노선에 구간을 제거 - 종점 하행역 제거")
    @Test
    void removeLineSection_1() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, ADD_SECTION_DISTANCE));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 종점 상행역 제거 요청을 하면
     * Then 다음으로 오던 역이 종점이 된다
     */
    @DisplayName("지하철 노선에 구간을 제거 - 종점 상행역 제거")
    @Test
    void removeLineSection_2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, ADD_SECTION_DISTANCE));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 중간역 제거 요청을 하면
     * Then 노선이 재배치된다
     */
    @DisplayName("지하철 노선에 구간을 제거 - 중간역이 제거될 경우 노선 재배치")
    @Test
    void removeLineSection_3() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, ADD_SECTION_DISTANCE));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    /**
     * Given 지하철 노선의 구간이 하나만 있을때
     * When 지하철 노선의 역중 하나를 제거하면
     * Then 노선의 구간에 변화가 없다
     */
    @DisplayName("지하철 노선에 구간을 제거 - 마지막 구간을 제거하려고 할때")
    @Test
    void removeLineSection_4() {
        // given

        // when
        ExtractableResponse<Response> removeSectionResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);
        assertThat(removeSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청하고
     * When 지하철 노선에 없는 역을 제거하면
     * Then 노선의 구간에 변화가 없다
     */
    @DisplayName("지하철 노선에 구간을 제거 - 노선에 등록되지 않은 역을 제거할 때")
    @Test
    void removeLineSection_5() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, ADD_SECTION_DISTANCE));

        // when
        Long 부산역 = 지하철역_생성_요청("부산역").jsonPath().getLong("id");

        ExtractableResponse<Response> removeSectionResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 부산역);
        assertThat(removeSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
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
