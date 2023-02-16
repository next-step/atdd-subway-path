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
class SectionAcceptanceTest extends AcceptanceTest {
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
    @DisplayName("지하철 구간 등록 - 정상1 : 지하철 노선에 구간을 등록")
    @Test
    void addLineSection_ValidCase1() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 구간을 생성한 뒤
     * When 생성된 구간의 상행역 또는 하행역을 기준으로 그 사이에 새로운 구간을 추가하면
     *   - 단, 새로운 구간의 길이는 기존 구간의 길이보다 작아야한다.
     * Then 새로운 두 개의 구간이 생성된다. (상행역 - 추가된역 & 추가된역 - 하행역)
     *   - 노선의 총 길이 = 기존 구간의 길이
     */
    @DisplayName("지하철 구간 등록 - 정상2 : 기존 구간 사이에 새로운 구간 추가")
    @Test
    void addLineSection_ValidCase2() {

    }

    /**
     * Given 지하철 노선에 구간을 생성한 뒤
     * When 새로운 역을 상행 종점으로 등록할 경우
     * Then 새로운 두 개의 구간이 생성된다.
     *   - 추가된 역이 해당 노선의 상행 종점역이 된다.
     *   - 노선의 총 길이 = 기존 구간 길이 + 새로운 구간 길이
     */
    @DisplayName("지하철 구간 등록 - 정상3 : 새로운 역이 상행 종점인 구간 추가")
    @Test
    void addLineSection_ValidCase3() {

    }

    /**
     * Given 지하철 노선에 구간을 생성한 뒤
     * When 새로운 역을 하행 종점으로 등록할 경우
     * Then 새로운 두 개의 구간이 생성된다.
     *   - 추가된 역이 해당 노선의 하행 종점역이 된다.
     *   - 노선의 총 길이 = 기존 구간 길이 + 새로운 구간 길이
     */
    @DisplayName("지하철 구간 등록 - 정상4 : 새로운 역이 하행 종점인 구간 추가")
    @Test
    void addLineSection_ValidCase4() {

    }

    /**
     * Given 지하철 노선에 구간을 생성한 뒤
     * When 생성된 구간의 상행역 또는 하행역을 기준으로 그 사이에 새로운 구간을 추가하면
     *    - 단, 새로운 구간의 길이는 기존 구간의 길이보다 크거나 같다.
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("지하철 구간 등록 - 예외1 : 기존 구간 사이에 새로운 구간을 추가하려는데, 새로운 구간의 길이가 기존 구간의 길이 이상인 경우")
    @Test
    void addLineSection_InvalidCase1() {

    }

    /**
     * Given 지하철 노선에 구간을 생성한 뒤
     * When 상,하행역 모두 이미 노선에 존재하는 새로운 구간을 추가하려는 경우
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("지하철 구간 등록 - 예외2: 새로운 구간의 상,하행역 모두 이미 노선에 등록된 경우")
    @Test
    void addLineSection_InvalidCase2() {

    }

    /**
     * Given 지하철 노선에 구간을 생성한 뒤
     * When 상,하행역 모두 노선에 존재하지 않는 새로운 구간을 추가하려는 경우
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("지하철 구간 등록 - 예외3: 새로운 구간의 상,하행역 모두 노선에 포함되지 않은 경우")
    @Test
    void addLineSection_InvalidCase3() {

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
}
