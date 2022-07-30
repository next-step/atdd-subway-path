package nextstep.subway.acceptance;

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

        강남역 = 지하철역_생성_요청("강남역").jsonPath()
                .getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath()
                .getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath()
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
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                .getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        var response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath()
                .getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
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
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                .getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        var response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath()
                .getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선이 구간이 주어지고
     * When 기존 구간의 역을 기준으로 역 사이에 새로운 구간을 추가하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 역 사이에 새로운 역을 등록")
    @Test
    void addLineSection_between_two_stations() {
        // Given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                .getLong("id");

        // When
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParamsWithDistance(강남역, 정자역, 5));

        // Then
        var response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath()
                .getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
        assertThat(response.jsonPath()
                .getInt("distance")).isEqualTo(10);
    }

    /**
     * Given 지하철 노선이 구간이 주어지고
     * When 기존 구간의 역을 기준으로 새로운 역을 상행 종점으로 추가하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 새로운 역을 상행 종점으로 등록")
    @Test
    void addLineSection_new_up_station() {
        // Given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                .getLong("id");

        // When
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParamsWithDistance(정자역, 강남역, 4));

        // Then
        var response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath()
                .getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
        assertThat(response.jsonPath()
                .getInt("distance")).isEqualTo(14);
    }

    /**
     * Given 지하철 노선이 구간이 주어지고
     * When 기존 구간의 역을 기준으로 새로운 역을 하행 종점으로 추가하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 새로운 역을 하행 종점으로 등록")
    @Test
    void addLineSection_new_down_station() {
        // Given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                .getLong("id");

        // When
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParamsWithDistance(양재역, 정자역, 6));

        // Then
        var response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath()
                .getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
        assertThat(response.jsonPath()
                .getInt("distance")).isEqualTo(16);
    }

    /**
     * Given 지하철 노선이 구간이 주어지고
     * When 기존 구간의 역을 기준으로 역 사이에 새로운 역을 등록할 때 기존 역 사이 길이보다 크거나 같은 경우
     * Then 오류가 발생한다.
     */
    @DisplayName("지하철 노선에 새로운 역을 등록할때 기존 역 사이 길이보다 크거나 같은 경우")
    @Test
    void addLineSection_fail_because_of_distance() {
        // Given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                .getLong("id");

        // When
        var result = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParamsWithDistance(강남역, 정자역, 10));

        // Then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 구간이 주어지고
     * When 상행역과 하행역이 이미 노선에 모두 등록되어 있다면
     * Then 오류가 발생한다.
     */
    @DisplayName("지하철 노선에 새로운 역을 등록할때 기존 역 사이 길이보다 크거나 같은 경우")
    @Test
    void addLineSection_fail_all_stations_is_already_included() {
        // When
        var result = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParamsWithDistance(강남역, 양재역, 10));

        // Then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 구간이 주어지고
     * When 상행역과 하행역 둘 중 하나도 포함되어 있지 않는 경우
     * Then 오류가 발생한다.
     */
    @DisplayName("지하철 노선에 새로운 역을 등록할때 상행역과 하행역 둘 중 하나도 포함되어 있지 않는 경우")
    @Test
    void addLineSection_fail_all_stations_is_not_included() {
        // Given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                .getLong("id");
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath()
                .getLong("id");

        // When
        var result = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParamsWithDistance(정자역, 판교역, 10));

        // Then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간 구간 제거를 요청하면
     * Then 노선에 중간구간이 제거된다.
     */
    @DisplayName("지하철 노선에 중간구간을 제거한다.")
    @Test
    void removeLineSection_in_middle() {
        // Given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                .getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParamsWithDistance(양재역, 정자역, 5));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // Then
        var response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath()
                .getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
        assertThat(response.jsonPath()
                .getInt("distance")).isEqualTo(15);
    }

    /**
     * Given & When 구간이 하나인 지하철 노선이 주어질때 지하철 노선의 마지막 구간을 제거하면
     * Then 오류가 발생한다.
     */
    @DisplayName("지하철 노선에서 구간이 하나인데 마지막 구간을 제거한다.")
    @Test
    void removeLineSection_fail_only_one_section() {
        // Given & When
        var result = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // Then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선에 없는 구간을 제거하려하면
     * Then 오류가 발생한다.
     */
    @DisplayName("지하철 노선에서 없는 구간을 제거한다.")
    @Test
    void removeLineSection_fail_not_included() {
        // Given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                .getLong("id");

        // When
        var result = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // Then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

    private Map<String, String> createSectionCreateParamsWithDistance(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
