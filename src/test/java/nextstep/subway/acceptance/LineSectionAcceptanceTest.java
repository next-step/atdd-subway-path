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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 양재-정자 구간을 추가하고
     * When 지하철 노선에 같은 구간 길이인 양재-논현 구간을 추가하면
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 노선에 기존 구간 길이보다 크거나 같은 구간을 등록")
    @Test
    void addLineSection_moreThenDistance() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        Long 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 논현역));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("기존 구간 사이에 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    /**
     * When 지하철 노선에 상행역과 하행역 모두 이미 등록된 구간을 추가하면
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 노선에 상행역과 하행역 모두 이미 등록된 구간을 등록")
    @Test
    void addLineSection_alreadyEnrollStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("상행 역과 하행 역이 이미 노선에 모두 등록되어 있다면 등록할 수 없습니다.");
    }

    /**
     * When 지하철 노선에 상행역과 하행역 둘 중 하나도 등록되지 않은 구간을 추가하면
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 노선에 상행역과 하행역 둘 중 하나도 등록되지 않은 구간을 등록")
    @Test
    void addLineSection_notEnrollStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 판교역));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("상행 역과 하행 역 둘 중 하나도 포함되어 있지 않으면 등록할 수 없습니다.");
    }

    /**
     * When 지하철 노선의 강남-양재 구간에 새로운 역(정자역)을 갖는 강남-정자 구간을 등록하면
     * Then 강남-정자 구간과 정자-양재 구간이 생성된다.
     */
    @Test
    @DisplayName("기존 구간 사이 새로운 역을 갖는 구간 등록")
    void addLineSection_middleStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 지하철 노선의 상행 종점역을 하행역으로 하는 신논현-강남 구간을 등록하면
     * Then 신논현역이 지하철 노선의 상행 종점역이 된다.
     */
    @Test
    @DisplayName("노선의 상행 역을 하행역으로 갖는 구간 등록")
    void addLineSection_frontStation() {
        // when
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("stations[0].id")).isEqualTo(신논현역);
    }

    /**
     * When 지하철 노선의 하행 종점역을 상행역으로 하는 양재-판교 구간을 등록하면
     * Then 판교역이 지하철 노선의 하행 종점역이 된다.
     */
    @Test
    @DisplayName("노선의 하행 종점역을 상행역으로 하는 구간 등록")
    void addLineSection_lastStation() {
        // when
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 판교역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("stations[-1].id")).isEqualTo(판교역);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 등록된 구간이 하나 이하인 노선에서 마지막 구간을 제거하면
     * Then 예외가 발생한다.
     */
    @Test
    @DisplayName("등록된 구간이 하나 이하인 노선에서 구간을 제거")
    void removeLineSection_lessThanOneSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("노선에 등록된 구간이 하나 이하일 경우 구간을 제거할 수 없습니다.");
    }

    /**
     * When 노선에 등록되지 않은 역을 제거하면
     * Then 예외가 발생한다.
     */

    /**
     * Given 지하철 노선에 새로운 양재-정자 구간 추가를 요청 하고
     * When 하행 종점역 제거를 요청 하면
     * Then 노선에서 제거되고, 양재역이 하행 종점역이 된다.
     */

    /**
     * Given 지하철 노선에 새로운 양재-정자 구간 추가를 요청 하고
     * When 상행 종점역 제거를 요청 하면
     * Then 노선에서 제거되고, 양재역이 하행 종점역이 된다.
     */

    /**
     * Given 지하철 노선에 새로운 양재-정자 구간 추가를 요청 하고
     * When 중간역인 양재역 제거를 요청 하면
     * Then 노선에 양재역 구간이 제거되고, 강남-정자 구간으로 재배치 된다.
     */

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
}
