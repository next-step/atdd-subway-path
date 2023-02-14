package nextstep.subway.ui.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.ui.acceptance.LineSteps.*;
import static nextstep.subway.ui.acceptance.StationSteps.지하철역_생성_요청;
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

        Map<String, String> lineCreateParams = 지하철_노선_요청_생성("신분당선", 강남역, 양재역, 10);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 하행 종점 구간을 추가 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 하행 종점역 구간을 등록")
    @Test
    void addLineSectionOfFinalDownStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> saveResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, 지하철_구간_요청_생성(양재역, 정자역, 5));

        // then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선 상행 종점 구간을 추가 요청을 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 상행 종점역 구간을 등록")
    @Test
    void addLineSectionOfFinalUpStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> saveResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, 지하철_구간_요청_생성(정자역, 강남역, 5));

        // then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선 중간에 구간을 추가 요청을 하면
     * Then 노선에 새로운 구간이 추가된다
     * Then 새로운 구간의 길이로 수정된다
     */
    @DisplayName("지하철 노선 중간에 구간을 등록")
    @Test
    void addLineSectionOfMiddleStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> saveResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, 지하철_구간_요청_생성(강남역, 정자역, 5));

        // then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 지하철 노선 중간에 기존 구간보다 긴 구간을 추가 요청을 하면
     * Then 노선에 구간 추가가 실패된다
     */
    @DisplayName("지하철 노선 중간에 기존 구간보다 긴 구간을 등록")
    @Test
    void addLineSectionOfLongerMiddleStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> saveResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, 지하철_구간_요청_생성(강남역, 정자역, 10));

        // then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }


    /**
     * When 지하철 노선에 상행, 하행이 중복된 구간을 추가 요청을 하면
     * Then 노선에 구간 추가가 실패된다
     */
    @DisplayName("지하철 노선에 상행, 하행이 중복된 구간을 등록")
    @Test
    void addLineSectionOfDuplicateStation() {
        // when
        ExtractableResponse<Response> saveResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, 지하철_구간_요청_생성(강남역, 양재역, 5));

        // then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 지하철 노선에 상행, 하행이 포함되지 않은 구간을 추가 요청을 하면
     * Then 노선에 구간 추가가 실패된다
     */
    @DisplayName("지하철 노선에 상행, 하행이 포함되지 않은 구간을 등록")
    @Test
    void addLineSectionOfUnknownStation() {
        // when
        Long 수원역 = 지하철역_생성_요청("수원역").jsonPath().getLong("id");
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> saveResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, 지하철_구간_요청_생성(수원역, 정자역, 5));

        // then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 하행 종점 구간 제거를 요청 하면
     * Then 노선에 구간이 제거되고
     * Then 이전역이 종점역이 된다.
     */
    @DisplayName("지하철 노선에 하행 종점 구간을 제거")
    @Test
    void removeLineSectionInFinalDown() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 지하철_구간_요청_생성(양재역, 정자역, 5));

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 상행 종점 구간 제거를 요청 하면
     * Then 노선에 구간이 제거되고
     * Then 다음역이 종점역이 된다.
     */
    @DisplayName("지하철 노선에 상행 종점 구간을 제거")
    @Test
    void removeLineSectionInFinalUp() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 지하철_구간_요청_생성(양재역, 정자역, 5));

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 중간 구간 제거를 요청하면
     * Then 노선에 구간이 제거되고
     * Then 제거된 역의 상, 하행의 구간의 길의의 합으로 재배치된다.
     */
    @DisplayName("지하철 노선에 상행 종점 구간을 제거")
    @Test
    void removeLineSectionInMiddle() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 지하철_구간_요청_생성(양재역, 정자역, 5));

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
        assertThat(response.jsonPath().getInt("totalDistance")).isEqualTo(15);
    }

    /**
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 구간 제거를 실패한다.
     */
    @DisplayName("구간이 하나인 지하철 노선에 구간을 제거하면 실패한다.")
    @Test
    void removeLeftOneLineSection() {
        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    private Map<String, String> 지하철_노선_요청_생성(String lineName, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", lineName);
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Map<String, String> 지하철_구간_요청_생성(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
