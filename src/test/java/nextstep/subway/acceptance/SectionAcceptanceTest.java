package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    // given
    private Long 장승배기역;
    private Long 상도역;
    private int distance = 10;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        장승배기역 = 지하철역_생성_요청("장승배기역").jsonPath().getLong("id");
        상도역 = 지하철역_생성_요청("상도역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, distance));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 기존 구간에 새로운 구간을 추가하면
     * Then 새로운 구간이 추가되고, 기존 구간의 길이에서 새로운 구간의 길이를 빼면, 새롭게 추가된 역 까지의 거리이다.
     */
    @DisplayName("기존 구간에 새로운 구간을 추가")
    @Test
    void addLineSection_BetweenExistingSection() {
        // when
        Long 신사역 = 지하철역_생성_한다("신사역").jsonPath().getLong("id");
        int 강남역_신사역_거리 = 6;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 신사역, 강남역_신사역_거리));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 신사역, 양재역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(10),
                () -> assertThat(distance - 강남역_신사역_거리).isEqualTo(4)
        );
    }

    /**
     * When 기존 구간에 새로운 역을 상행 종점으로 등록 하면
     * Then 새로운 구간이 추가되고, 새로운 역이 상행 종점이 된다.
     */
    @DisplayName("기존 구간에 새로운 구간을 추가")
    @Test
    void addLineSection_BetweenExistingSection_WithLastUpStation() {
        // when
        Long 신논현역 = 지하철역_생성_한다("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, distance));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        JsonPath jsonPath = response.jsonPath();
        System.out.println(jsonPath);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class).get(0)).isEqualTo(신논현역)
        );
    }

    /**
     * When 기존 구간에 새로운 역을 하행 종점으로 등록 하면
     * Then 새로운 구간이 추가되고, 새로운 역이 하행 종점이 된다.
     */
    @DisplayName("기존 구간에 새로운 구간을 추가")
    @Test
    void addLineSection_BetweenExistingSection_WithLastDownStation() {
        // when
        Long 신사역 = 지하철역_생성_한다("신사역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 신사역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(16),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class).get(response.jsonPath().getList("stations.id").size() - 1)).isEqualTo(신사역)
        );
    }

    /**
     * When 기존 구간에 추가할 때 기존구간과 길이가 같은 새로운 구간을 추가하면
     * Then 오류가 발생한다.
     */
    @DisplayName("기존 구간에 길이가 같은 새로운 구간 추가")
    @Test
    void addLineSection_BetweenExistingSection_WithEqualDistance() {
        // when
        Long 신사역 = 지하철역_생성_한다("신사역").jsonPath().getLong("id");
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 신사역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 기존 구간에 추가할 때 기존구간과 길이가 더 큰 새로운 구간을 추가하면
     * Then 오류가 발생한다.
     */
    @DisplayName("기존 구간에 길이가 더 긴 새로운 구간 추가")
    @Test
    void addLineSection_BetweenExistingSection_WithLongerDistance() {
        // when
        Long 신사역 = 지하철역_생성_한다("신사역").jsonPath().getLong("id");
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 신사역, 11));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 기존 구간에 추가할 때 상하행역 모두 이미 등록이 되어있으면
     * Then 오류가 발생한다.
     */
    @DisplayName("기존 구간에 이미 등록되어 있는 상하행역에 대한 구간 추가")
    @Test
    void addLineSection_AlreadyExistsUpAndDownStation() {
        // when
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    /**
     * When 기존 구간에 추가할 때 상하행역 모두 포함되어 있지 않으면
     * Then 오류가 발생한다.
     */
    @DisplayName("기존 구간에 모두 포함되지 않는 상하행역에 대한 구간 추가")
    @Test
    void addLineSection_NotIncludedUpAndDownStation() {
        // when
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(장승배기역, 상도역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, distance));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 종점이 제거되면
     * Then 다음으로 오던 역이 종점이 된다.
     */
    @DisplayName("지하철 노선의 종점을 제거")
    @Test
    void removeLineSection_WithLastEndSection() {
        // given
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, distance));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(getLastStationId(response.jsonPath().getList("stations.id", Long.class))).isEqualTo(강남역)
        );
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간역이 제거되면
     * Then 지하철 역을 재배치 하고, 거리는 두 구간의 합이다.
     */
    @DisplayName("지하철 노선의 중간역을 제거")
    @Test
    void removeLineSection_WithMiddleSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance + 6)
        );
    }

    /**
     * When 지하철 노선의 구간이 하나일때 제거 요청을 하면
     * Then 오류가 발생한다.
     */
    @DisplayName("지하철 노선의 구간이 하나일 때 제거")
    @Test
    void removeLineSection_WithOnlyOneSection() {
        // when
        var response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 구간이 하나도 등록되어 있지 않으면
     * Then 오류가 발생한다.
     */
    @DisplayName("지하철 노선의 구간이 비어있을 때 제거")
    @Test
    void removeLineSection_WithEmptySection() {
        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);
        var response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

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
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Long getLastStationId(List<Long> list) {
        return list.get(list.size() - 1);
    }
}
