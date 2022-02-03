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
        params.put("distance", distance + "");
        return params;
    }

    /**
     * when 노선 구간의 시작역을 기준으로 새로운 구간을 추가하면
     * then 새로운 구간이 추가된다.
     */
    @DisplayName("지하철 노선 구간의 시작 역을 기준으로 구간을 추가")
    @Test
    void addLineSection2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * when 노선 구간의 상행역을 새로운 구간의 하행역으로 구간을 추가하면
     * then 새로운 구간이 추가된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우 기존 구간 앞에 구간이 추가된다.")
    @Test
    void addLineSection3() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * when 노선 구간의 하행역을 새로운 구간의 상행역으로 구간을 추가하면
     * then 새로운 구간이 추가된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우 기존 구간 뒤에 구간이 추가된다.")
    @Test
    void addLineSection4() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * when 기존 구간과 길이가 같거나 긴 구간을 중간에 추가하면
     * then 구간 등록이 실패한다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addLineSectionFail() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * when 기존 구간과 동일한 구간을 추가하면
     * then 구간 등록이 실패한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addLineSectionFail2() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 3));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * when 기존 구간에 포함되지 않는 역으로 이루어진 구간을 추가하면
     * then 구간 등록이 실패한다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addLineSectionFail3() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 논현역, 3));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
