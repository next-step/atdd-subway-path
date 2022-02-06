package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.*;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청_후_ID_반환;
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

        강남역 = 지하철역_생성_요청_후_ID_반환("강남역");
        양재역 = 지하철역_생성_요청_후_ID_반환("양재역");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청_후_ID_반환(lineCreateParams);
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청_후_ID_반환("정자역");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        List<Long> 지하철역_리스트 = new ArrayList<>();
        지하철역_리스트.add(강남역);
        지하철역_리스트.add(양재역);
        지하철역_리스트.add(정자역);
        지하철_노선_조회_요청_후_역_검증(신분당선, 지하철역_리스트);
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
        Long 정자역 = 지하철역_생성_요청_후_ID_반환("정자역");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        List<Long> 지하철역_리스트 = new ArrayList<>();
        지하철역_리스트.add(강남역);
        지하철역_리스트.add(양재역);
        지하철_노선_조회_요청_후_역_검증(신분당선, 지하철역_리스트);

    }

    /**
     * Given 노선을 생성하고 해당 노선에 종점역을 추가한 후
     * When 해당 노선의 전체 구간 중간에 구간 추가를 요청하면
     * Then 구간 추가가 성공한다.
     */
    @DisplayName("노선 중간에 구간 추가(성공 케이스)")
    @Test
    void addMiddleStationsTest() {
        // given
        Long 정자역 = 지하철역_생성_요청_후_ID_반환("정자역");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 3));

        // then
        List<Long> 지하철역_리스트 = createListOfStations(강남역, 양재역);
        지하철_노선_조회_요청_후_역_검증(신분당선, 지하철역_리스트);
    }

    /**
     * Given 노선을 생성하고 해당 노선에 종점역을 추가한 후
     * When 해당 노선의 전체 구간 중간에 길이가 같거나 더 긴 구간 추가를 요청하면
     * Then 구간 추가가 실패한다.
     */
    @DisplayName("노선 중간에 길이가 더 긴 구간 추가(실패 케이스)")
    @Test
    void addMiddleStationsFailedTest() {
        // given
        Long 정자역 = 지하철역_생성_요청_후_ID_반환("정자역");

        // when
        ExtractableResponse<Response> createResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 11));

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선을 생성하고 해당 노선에 종점역을 추가한 후
     * When 해당 노선의 구간에 존재하지 않는 역들을 구간으로 추가할 경우
     * Then 구간 추가가 실패한다.
     */
    @DisplayName("노선에 상 하행 역 존재하지 않는 구간 추가 실패")
    @Test
    void addSectionWithoutExistStationsTest() {
        // given
        Long 정자역 = 지하철역_생성_요청_후_ID_반환("정자역");
        Long 미금역 = 지하철역_생성_요청_후_ID_반환("미금역");

        // when
        ExtractableResponse<Response> createResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역, 3));

        // then 구간 추가 실패해야 한다.
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

    public Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        return createSectionCreateParams(upStationId, downStationId, 6);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private List<Long> createListOfStations(Long ... stations){
        return Arrays.asList(stations);
    }
}
