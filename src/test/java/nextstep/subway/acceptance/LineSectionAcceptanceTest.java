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
    private static final String GANGNAM_STATION = "강남역";
    private static final String YANGJAE_STATION = "양재역";
    private static final String JUNGJA_STATION = "정자역";
    private static final String PATH_ID = "id";
    private static final String PATH_STATIONS_ID = "stations.id";
    private static final int DISTANCE_TEN = 10;
    private static final int DISTANCE_FIVE = 5;
    private static final String SHINBUNDANG_LINE = "신분당선";
    private static final String BACKGROUND_COLOR_BLUE = "bg-color-blue";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_COLOR = "color";
    private static final String PARAM_UP_STATION_ID = "upStationId";
    private static final String PARAM_DOWN_STATION_ID = "downStationId";
    private static final String PARAM_DISTANCE = "distance";

    private Long 신분당선;
    private Long 강남역;
    private Long 양재역;

    /**
     * given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철역_생성_요청(GANGNAM_STATION).jsonPath().getLong(PATH_ID);
        양재역 = 지하철역_생성_요청(YANGJAE_STATION).jsonPath().getLong(PATH_ID);

        Map<String, String> lineCreateParams = createLineCreateParams(SHINBUNDANG_LINE, BACKGROUND_COLOR_BLUE, 강남역, 양재역, DISTANCE_TEN);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong(PATH_ID);
    }

    /**
     * when 지하철 노선에 새로운 구간 추가를 요청 하면
     * then 노선에 새로운 구간이 추가된다
     */
    @Test
    void 지하철_노선에_구간_추가() {
        // when
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_FIVE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(PATH_STATIONS_ID, Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * given 지하철 노선에 새로운 구간 추가를 요청 하고
     * when 지하철 노선의 마지막 구간 제거를 요청 하면
     * then 노선에 구간이 제거된다
     */
    @Test
    void 지하철_노선에_구간_삭제() {
        // given
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_FIVE));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(PATH_STATIONS_ID, Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * when : 기존 구간에 포함된 상행역에서 출발하는 새로운 구간을 추가한다.
     * when : 새로운 구간의 거리는 기존 구간의 거리보다 짧다.
     * then : 결과로 기존 구간은 삭제되고
     * then : '기존 구간의 상행역 - 새로운 구간의 하행역', '새로운 구간의 하행역 - 기존 구간의 하행역'으로 분리되어 노선에 추가된다.
     */
    @Test
    void 기존_구간의_상행역에서_출발하는_새로운_구간을_만든다() {
        // when
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(PATH_STATIONS_ID, Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    private Map<String, String> createLineCreateParams(String lineName, String lineColor, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put(PARAM_NAME, lineName);
        lineCreateParams.put(PARAM_COLOR, lineColor);
        lineCreateParams.put(PARAM_UP_STATION_ID, String.valueOf(upStationId));
        lineCreateParams.put(PARAM_DOWN_STATION_ID, String.valueOf(downStationId));
        lineCreateParams.put(PARAM_DISTANCE, String.valueOf(distance));
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_UP_STATION_ID, String.valueOf(upStationId));
        params.put(PARAM_DOWN_STATION_ID, String.valueOf(downStationId));
        params.put(PARAM_DISTANCE, String.valueOf(distance));
        return params;
    }
}
