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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선의 기존 지하철 구간에 새로운 구간 추가를 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 기존 구간에 새로운 구간을 등록")
    @Test
    void addExistLineSection() {
        Long 고속터미널역 = 지하철역_생성_요청("고속터미널역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 고속터미널역, 6));

        // when
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 교대역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(강남역, 교대역, 양재역, 고속터미널역);
    }

    /**
     * When 지하철 노선에 새로운 역이 상행 종점인 구간 추가를 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 새로운 역이 상행 종점인 구간을 등록")
    @Test
    void addUpStationLineSection() {
        // when
        Long 고속터미널역 = 지하철역_생성_요청("고속터미널역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(고속터미널역, 강남역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(고속터미널역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선에 새로운 역이 하행 종점인 구간 추가를 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 새로운 역이 하행 종점인 구간을 등록")
    @Test
    void addDownStationLineSection() {
        // when
        Long 고속터미널역 = 지하철역_생성_요청("고속터미널역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 고속터미널역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(강남역, 양재역, 고속터미널역);
    }

    /**
     * Given 기존 구간의 길이보다 크거나 같은 구간을 생성하고
     * When 지하철 노선의 기존 지하철 구간에 새로운 구간 추가를 요청하면
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선에 기존 구간의 길이와 같거나 큰 경우 에러 발생")
    @Test
    void addLineSectionIfDistanceGreaterThanOrEqual() {
        // when
        Long 고속터미널역 = 지하철역_생성_요청("고속터미널역").jsonPath().getLong("id");

        // then
        assertThat(지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 고속터미널역, 10)).statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 고속터미널역, 11)).statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선에 이미 등록된 상행역과 하행역을 가진 구간을 생성하고
     * When 지하철 노선에 새로운 구간 추가를 요청하면
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선에 이미 등록된 상행역과 하행역을 가진 구간을 등록할 때 에러 발생")
    @Test
    void addDuplicateLineSection() {
        // given, when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선
                , createSectionCreateParams(강남역, 양재역, 5));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선에 지하철 구간에 상행역과 하행역 모두 포함되지 않은 구간을 생성하고
     * When 지하철 노선에 새로운 구간 추가를 요청하면
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선에 상행역과 하행역 모두 포함되지 않은 구간을 등록할 때 에러 발생")
    @Test
    void addIncompleteLineSection() {
        // when
        Long 연신내역 = 지하철역_생성_요청("연신내역").jsonPath().getLong("id");
        Long 독바위역 = 지하철역_생성_요청("독바위역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선
                , createSectionCreateParams(연신내역, 독바위역, 6));

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
        params.put("distance", String.valueOf(distance));
        return params;
    }
}
