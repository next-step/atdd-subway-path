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
import static org.junit.jupiter.api.Assertions.assertAll;

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
     * When 새로운 역을 기존 노선의 사이로 구간 추가를 요청 하면 (기존 구간의 상행역과 신규 노선의 하행역이 일치하는 경우)
     * Then 노선 중간에 새로운 구간이 추가된다.
     * 신분당선 : 강남역 - 양재역
     */
    @DisplayName("새로운 역을 기존 노선 사이로 구간을 등록")
    @Test
    void addNewSectionBetweenLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 새로운 역을 상행 종점으로 구간 추가를 요청 하면 (기존 구간의 상행역과 신규 노선의 하행역이 일치하는 경우)
     * Then 새로운 구간의 상행역이 상행 종점으로 구간이 추가된다
     */
    @DisplayName("새로운 역을 상행 종점으로 구간을 등록")
    @Test
    void addNewUpStationLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * When 기존 노선에 존재하지 않는 역으로 구성된 구간을 신규로 등록 요청 하면
     * Then 지하철 구간 요청 시 예외를 발생하고
     * Then 기존 노선을 조회 시 구간은 이전 상태 그대로 유지된다.
     */
    @DisplayName("노선에 존재하지 않는 역으로 구성된 구간을 신규로 등록하면 예외")
    @Test
    void addSectionExceptionWhenAddNotExistsStation() {
        // when
        long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        ExtractableResponse<Response> addSectionResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 판교역));

        ExtractableResponse<Response> findLineResponse = 지하철_노선_조회_요청(신분당선);
        String exceptionMessage = addSectionResponse.jsonPath().getString("message");

        assertAll(
                // then
                () -> assertThat(addSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(exceptionMessage).isEqualTo("상행역과 하행역 둘 중 하나라도 노선에 존재해야 합니다."),
                // then
                () -> assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(findLineResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역)
        );
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
     * Given 지하철 노선에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 중간 구간 제거를 요청 하면
     * Then 노선에 구간이 제거되고
     * Then 노선의 길이는 합산된다
     */
    @DisplayName("지하철 노선에 중간 구간을 제거")
    @Test
    void removeLineMiddleSection() {
        // given

        // when

        // then

        // then
    }

    /**
     * When 지하철 노선의 구간 제거를 요청하면
     * Then 노선이 정상적으로 제거되지 않는다. (구간이 1개일 경우 구간 제거 불가)
     */
    @DisplayName("지하철 노선에 구간이 1개일 경우 구간 제거 요청 시 예외")
    @Test
    void removeLineExceptionWhenOnlyOneLine() {
        // when

        // then
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
}
