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
        신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역, 양재역, 10).jsonPath()
                .getLong("id");
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

    /**
     * When 지하철 노선 구간 사이에 새로운 구간을 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @Test
    void 지하철_노선_구간_사이에_구간을_등록() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 3));

        // then
        var 지하철_노선_목록_조회_응답 = 지하철_노선_목록을_조회한다();
        지하철_노선이_포함되어있다(지하철_노선_목록_조회_응답, 강남역, 정자역, 양재역);
    }


    /**
     * When 지하철 노선 상행 종점역에 새로운 구간을 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */

    /**
     * When 지하철 노선 상행 종점역에 새로운 구간을 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */

    /**
     * When 지하철 노선 구간 사이에 거리가 비슷한 새로운 구간을 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가되지 않는다
     */

    /**
     * When 지하철 노선 구간 사이에 똑같은 구간을 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가되지 않는다
     */
    @Test
    void 지하철_노선_구간_사이에_같은_구간을_등록() {
        // when
        var 구간_생성_요청_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 3));

        // then
        구간_추가를_실패한다(구간_생성_요청_응답);
    }

    private void 구간_추가를_실패한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선 구간 사이에 상행역, 하행역 포함되지 않는 구간을 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가되지 않는다
     */
    @Test
    void 지하철_노선_구간_사이에_서로_다른_구간을_등록() {
        // when
        var 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        var 송파역 = 지하철역_생성_요청("송파역").jsonPath().getLong("id");
        var 구간_생성_요청_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 송파역, 3));

        // then
        구간_추가를_실패한다(구간_생성_요청_응답);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    public static Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private void 지하철_노선이_포함되어있다(ExtractableResponse<Response> 지하철_노선_목록_조회_응답, Long... elements) {
        assertThat(지하철_노선_목록_조회_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(elements);
    }

    private ExtractableResponse<Response> 지하철_노선_목록을_조회한다() {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response;
    }
}
