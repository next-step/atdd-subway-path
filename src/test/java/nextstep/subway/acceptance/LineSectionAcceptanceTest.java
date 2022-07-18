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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {

    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 신규역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     * Given 신규로 추가할 역을 생성 한다
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        신규역 = 지하철역_생성_요청("신규역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 기존 구간의 상행 종점역과 동일한 하행역을 가지고
     * When 구간 생성 요청 하면
     * Then 구간 생성이 성공하고
     * Then 역 목록을 응답 받는다
     */
    @DisplayName("신규 구간의 하행역이 기존 구간의 상행 종점역과 동일할 경우 상행종점역 으로 구간 추가하기 ")
    @Test
    public void add_up_station_at_line_front() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신규역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_역_순서_확인(response, 신규역, 강남역, 양재역);
    }

    /**
     * When 기존 구간의 하행 종점역과 동일한 상행역을 가지고
     * When 구간 생성 요청 하면
     * Then 구간 생성이 성공하고
     * Then 역 목록을 응답 받는다
     */
    @DisplayName("신규 구간의 상행역이 기존 구간의 하행역과 동일할 경우 하행종점역 으로 구간 추가하기")
    @Test
    public void add_down_station_at_line_back() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 신규역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_역_순서_확인(response, 강남역, 양재역, 신규역);
    }

    /**
     * When 기존 구간의 상행 종점역과 동일한 상행역을 가지고
     * When 구간 생성 요청 하면
     * Then 구간 생성이 성공하고
     * Then (신규역 - 하행역)의 길이는 기존 구간의 길이에서 (상행역 - 신규역)의 길이를 뺸 길이로 할당하고
     * Then (상행역 - 신규역 - 하행역)의 순서로 배치되고
     * Then 역 목록을 응답 받는다
     */
    @DisplayName("신규 구간의 상행역이 기존 구간의 상행역과 동일할 경우 중간에 구간을 추가할 수 있다")
    @Test
    public void add_section_at_line_middle() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 신규역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_역_순서_확인(response, 강남역, 신규역, 양재역);
    }

    /**
     * When 기존 구간의 상행 종점역과 동일한 상행역을 가지고
     * When 새로운 하행역 기존의 구간 거리보다 같거나 클떄
     * When 구간 생성을 요청하면
     * Then 구간 생성이 실패한다
     */
    @DisplayName("역 사이에 새로운 역 추가할때, 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다")
    @Test
    public void add_section_fail_by_over_distance() {
        // when
        int overDistance = 11;
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 신규역, overDistance));

        // then
        assertAll(
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("기존의 구간 길이보다 긴 신규구간을 중간에 추가할 수 없습니다"),
                () -> assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance.toString());
        return params;
    }
}
