package nextstep.subway.acceptance;

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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[Acceptance] 지하철 구간 관리 기능")
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
     * Given 역과 구간을 추가로 등록하고
     * When 지하철 노선 구간 사이에 새로운 구간을 추가하면
     * Then 노선에 새로운 구간이 기존 구간들 사이에 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        Long 역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 역삼역, 5));

        var response = 지하철_노선_조회_요청(신분당선);

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 역삼역, 정자역);
        });
    }

    /**
     * When 지하철 노선에 새로운 구간을 상행 종점으로 추가하면
     * Then 노선에 새로운 구간이 상행 종점으로 추가된다.
     */
    @DisplayName("지하철 노선에 상행 종점 구간을 등록")
    @Test
    void addLineSection1() {
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 10));

        var response = 지하철_노선_조회_요청(신분당선);
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
        });
    }

    /**
     * When 지하철 노선에 새로운 구간을 하행 종점으로 추가하면
     * Then 노선에 마지막 구간에 하행 종점으로 추가된다.
     */
    @DisplayName("지하철 노선에 하행 종점 구간을 등록")
    @Test
    void addLineSection2() {
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        var response = 지하철_노선_조회_요청(신분당선);

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
        });
    }

    /**
     * When 지하철 노선에 기존 역 사이 길이보다 큰 구간을 추가하면
     * Then 구간을 등록할 수 없다.
     */
    @DisplayName("기존 역 사이 길이보다 큰 구간을 추가할 수 없다")
    @Test
    void addSectionException1() {
        var 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        var 강남역_정자역_구간_요청 = createSectionCreateParams(강남역, 정자역, 15);
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 강남역_정자역_구간_요청);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 이미 등록 되어있는 구간을 추가하면
     * Then 구간을 등록할 수 없다.
     */
    @DisplayName("이미 등록된 구간을 추가할 수 없다")
    @Test
    void addSectionException2() {
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 5));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 상행역과 하행역 둘 중 하나도 포함되어있지 않은 구간을 추가하면
     * Then 구간을 등록할 수 없다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나라도 포함되어 있지 않은 구간은 추가할 수 없다.")
    @Test
    void addSectionException3() {
        var 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        var 역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");

        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 역삼역, 15));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        var response = 지하철_노선_조회_요청(신분당선);

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
        });

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
}
