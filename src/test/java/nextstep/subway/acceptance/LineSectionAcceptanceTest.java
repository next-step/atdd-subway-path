package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
     * When 새로운 역을 하행 종점으로 추가요청하면
     * Then 노선 맨 끝에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 마지막 구간을 등록")
    @Test
    void addLastLineSection() {
        // when
        Long 양재시민의숲역 = 지하철역_생성_요청("양재시민의숲역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재시민의숲역));

        // then
        구간이_올바른_순서대로_존재하는지_검증(신분당선, List.of(강남역, 양재역, 양재시민의숲역));
    }

    /**
     * When 새로운 역을 상행 종점으로 추가요청하면
     * Then 노선 맨 처음에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 첫 번째 구간을 등록")
    @Test
    void addFirstLineSection() {
        // when
        var 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역));

        // then
        구간이_올바른_순서대로_존재하는지_검증(신분당선, List.of(신논현역, 강남역, 양재역));
    }

    /**
     * When 상행역을 기존 역, 하행역을 새로운 역으로 구간 사이에 추가 요청하면
     * Then 구간 사이에 새로운 구간이 추가된다
     */
    @DisplayName("구간 사이에 새로운 구간을 등록 (하행역이 신규역)")
    @Test
    void addLineSectionWithNewDownStationInMiddle() {
        // when
        var 강재역 = 지하철역_생성_요청("강재역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 강재역));

        // then
        구간이_올바른_순서대로_존재하는지_검증(신분당선, List.of(강남역, 강재역, 양재역));
    }

    @DisplayName("구간 사이에 새로운 구간을 등록 (상행역이 신규역)")
    @Test
    void addLineSectionWithNewUpStationInMiddle() {
        // when
        var 강재역 = 지하철역_생성_요청("강재역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강재역, 양재역));

        // then
        구간이_올바른_순서대로_존재하는지_검증(신분당선, List.of(강남역, 강재역, 양재역));
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 노선의 중간역 제거요청을 하면
     * Then 중간역이 제거된다
     */
    @DisplayName("노선의 중간 역 제거")
    @Test
    void removeLineSectionInMiddle() {
        // given
        var 양재시민의숲역 = 지하철역_생성_요청("양재시민의숲역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재시민의숲역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        구간이_올바른_순서대로_존재하는지_검증(신분당선, List.of(강남역, 양재시민의숲역));
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 첫 번째 역 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("노선의 첫 번째 역 제거")
    @Test
    void removeFirstLineSection() {
        // given
        Long 신사역 = 지하철역_생성_요청("신사역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신사역, 강남역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 신사역);

        // then
        구간이_올바른_순서대로_존재하는지_검증(신분당선, List.of(강남역, 양재역));
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 역 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("노선의 마지막 역 제거")
    @Test
    void removeLastLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        구간이_올바른_순서대로_존재하는지_검증(신분당선, List.of(강남역, 양재역));
    }

    /**
     * When 노선에 마지막 남은 구간의 상행역을 제거 요청하면
     * Then 구간 제거에 실패한다
     */
    @DisplayName("노선의 마지막 구간 제거 실패 (상행역)")
    @Test
    void removeLastRemainLineSectionWithUpStationFails() {
        // when
        var response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 노선에 마지막 남은 구간의 하행역을 제거 요청하면
     * Then 구간 제거에 실패한다
     */
    @DisplayName("노선의 마지막 구간 제거 실패 (하행역)")
    @Test
    void removeLastRemainLineSectionWithDownStationFails() {
        // when
        var response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개 이상의 노선이 존재할 때
     * When 노선에 존재하지 않는 역을 제거 시도하면
     * Then 구간 제거에 실패한다
     */
    @DisplayName("노선에 포함되지 않은 역 제거 실패")
    @Test
    void removeFailsWhenStationIsNotInLine() {
        // given
        var 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        var 뉴욕역 = 지하철역_생성_요청("뉴욕역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        var response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 뉴욕역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

    private void 구간이_올바른_순서대로_존재하는지_검증(Long lineId, List<Long> stationIds) {
        var response = 지하철_노선_조회_요청(lineId);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactlyElementsOf(stationIds)
        );
    }
}
