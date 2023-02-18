package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.domain.exception.line.LineErrorMessage.INVALID_DISTANCE;
import static nextstep.subway.domain.exception.line.LineErrorMessage.LINE_HAS_BOTH_STATION;
import static nextstep.subway.domain.exception.line.LineErrorMessage.NEW_SECTION_COULD_HAVE_ANY_REGISTERED_STATION;
import static nextstep.subway.utils.AssertionUtils.목록은_다음을_순서대로_포함한다;
import static nextstep.subway.utils.AssertionUtils.에러메시지는_다음과_같다;
import static nextstep.subway.utils.AssertionUtils.응답코드_200을_반환한다;
import static nextstep.subway.utils.AssertionUtils.응답코드_400을_반환한다;
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
    @DisplayName("지하철 노선에 새로운 역을 하행 종점역으로 갖는 구간을 등록한다")
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
     * given: 지하철 노선의 상행 종점역을 하행역으로 갖는 구간을 만들고
     * when : 지하철 노선에 구간 추가 요청을 하면
     * then : 노선에 새로운 구간이 추가되고
     * then : 역 목록 조회시 추가된 역이 순서대로 조회된다.
     */
    @DisplayName("지하철 노선에 새로운 역을 상행 종점역으로 갖는 구간을 등록한다")
    @Test
    void addFirstSection() {
        // given
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createLineCreateParams(신논현역, 강남역));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        // then
        응답코드_200을_반환한다(response);

        final List<Long> 조회된_역_목록 = response.jsonPath().getList("stations.id", Long.class);
        목록은_다음을_순서대로_포함한다(조회된_역_목록, 신논현역, 강남역, 양재역);
    }

    /**
     * given: 지하철 노선 중간의 역을 상행역으로 갖는 구간을 추가하고
     * when : 지하철 노선 목록을 조회하면
     * then : 역이 새로운 순서대로 조회된다.
     */
    @DisplayName("지하철 노선의 중간에 노선의 역을 상행역으로 갖는 구간을 등록한다")
    @Test
    void addMiddleSectionFromUpStation() {
        // given
        Long 신강남역 = 지하철역_생성_요청("신강남역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createLineCreateParams(강남역, 신강남역, 6));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        // then
        응답코드_200을_반환한다(response);

        final List<Long> 조회된_역_목록 = response.jsonPath().getList("stations.id", Long.class);
        목록은_다음을_순서대로_포함한다(조회된_역_목록, 강남역, 신강남역, 양재역);
    }

    /**
     * given: 지하철 노선 중간의 역을 하행역으로 갖는 구간을 추가하고
     * when : 지하철 노선 목록을 조회하면
     * then : 역이 새로운 순서대로 조회된다.
     */
    @DisplayName("지하철 노선의 중간에 노선의 역을 하행역으로 갖는 구간을 등록한다.")
    @Test
    void addMiddleSectionFromDownStation() {
        // given
        Long 신강남역 = 지하철역_생성_요청("신강남역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createLineCreateParams(신강남역, 양재역, 6));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        // then
        응답코드_200을_반환한다(response);

        final List<Long> 조회된_역_목록 = response.jsonPath().getList("stations.id", Long.class);
        목록은_다음을_순서대로_포함한다(조회된_역_목록, 강남역, 신강남역, 양재역);
    }

    /**
     * when : 지하철 노선의 중간 구간에 같거나 더 긴 길이를 갖는 새로운 구간을 추가하면
     * then : 오류가 발생한다.
     */
    @DisplayName("새로운 역 사이의 길이가 기존 역사이의 길이보다 같거나 큰 경우 등록할 수 없다")
    @Test
    void sectionCannotRegisteredWhenNewSectionIsLongerOrEqual() {
        // when
        Long 신강남역 = 지하철역_생성_요청("신강남역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createLineCreateParams(신강남역, 양재역));

        // then
        응답코드_400을_반환한다(response);
        에러메시지는_다음과_같다(response, INVALID_DISTANCE.getMessage());
    }

    /**
     * when : 상행역과 하행역이 모두 노선에 등록되어 있는 구간을 등록하면
     * then : 오류가 발생한다.
     */
    @DisplayName("상행역과 하행역이 모두 노선에 등록되어있는 경우 등록할 수 없다.")
    @Test
    void sectionCannotRegisteredWhenBothAreRegistered() {
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createLineCreateParams(강남역, 양재역, 5));

        // then
        응답코드_400을_반환한다(response);
        에러메시지는_다음과_같다(response, LINE_HAS_BOTH_STATION.getMessage());
    }

    /**
     * when : 상행역과 하행역이 모두 노선에 등록되어있지 않은 구간을 등록하면
     * then : 오류가 발생한다.
     */
    @DisplayName("상행역과 하행역 모두 노선에 등록되어있지 않는 경우 등록할 수 없다.")
    @Test
    void sectionCannotRegisteredWhenBothAreNotRegistered() {
        Long 서울대입구역 = 지하철역_생성_요청("서울대입구역").jsonPath().getLong("id");
        Long 봉천역 = 지하철역_생성_요청("봉천역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createLineCreateParams(서울대입구역, 봉천역));

        // then
        응답코드_400을_반환한다(response);
        에러메시지는_다음과_같다(response, NEW_SECTION_COULD_HAVE_ANY_REGISTERED_STATION.getMessage());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 하행 종점역 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 하행 종점역을 제거한다.")
    @Test
    void removeLastStation() {
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

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 상행 종점역 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 상행 종점역을 제거한다.")
    @Test
    void removeFirstStation() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간역 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 상행 종점역을 제거한다.")
    @Test
    void removeMiddleStation() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
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

    private Map<String, String> createLineCreateParams(final Long upStationId, final Long downStationId, final int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
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
