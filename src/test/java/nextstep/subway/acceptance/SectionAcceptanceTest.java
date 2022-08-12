package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.error.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
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
     * When 지하철 노선에 하행 종점으로 새로운 구간 추가를 요청하면
     * Then 노선의 마지막에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 하행 종점으로 구간을 등록")
    @Test
    void addLineSectionWithDownStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        요청이_정상적으로_처리되었는지_확인(response, HttpStatus.OK);
        지하철역_순서를_검증(response, List.of(강남역, 양재역, 정자역));
    }

    /**
     * When 지하철 노선에 역 사이로 새로운 구간 추가를 요청하면
     * Then 노선의 역과 역 사이에 구간이 추가된다
     */
    @DisplayName("지하철 노선에 역과 역 사이로 구간을 등록")
    @Test
    void addLineSectionBetweenStations() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        요청이_정상적으로_처리되었는지_확인(response, HttpStatus.OK);
        지하철역_순서를_검증(response, List.of(강남역, 정자역, 양재역));
    }

    /**
     * 새로운 지하철역(정자역) 을 생성하고
     * When 지하철 노선에 역과 역 사이의 구간보다 크거나 같은 길이의 새로운 구간 추가를 요청하면
     * Then 구간을 추가할 수 없다는 에러가 발생한다
     */
    @DisplayName("[Error] 지하철 노선에 역과 역 사이의 구간보다 크거나 같은 길이의 구간을 등록")
    @Test
    void addLineSectionWithInvalidDistance() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        final ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 10));

        // then
        요청이_정상적으로_처리되었는지_확인(지하철_노선에_지하철_구간_생성_응답, HttpStatus.BAD_REQUEST);
        에러메시지_확인(지하철_노선에_지하철_구간_생성_응답, ErrorCode.INVALID_SECTION_DISTANCE);
    }

    /**
     * When 지하철 노선에 이미 등록되어있는 상행역과 하행역에 대한 새로운 구간 추가를 요청하면
     * Then 구간을 추가할 수 없다는 에러가 발생한다
     */
    @DisplayName("[Error] 지하철 노선에 상행역, 하행역이 모두 등록되어있는 구간을 등록")
    @Test
    void addLineSectionWithExistsUpStationAndDownStation() {
        // when
        final ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역));

        // then
        요청이_정상적으로_처리되었는지_확인(지하철_노선에_지하철_구간_생성_응답, HttpStatus.BAD_REQUEST);
        에러메시지_확인(지하철_노선에_지하철_구간_생성_응답, ErrorCode.SECTION_ALREADY_EXISTS);
    }

    /**
     * Given 새로운 지하철역(정자역, 미금역)을 생성하고
     * When 지하철 노선에 상행역, 하행역이 모두 등록되어있지 않은 새로운 구간 추가를 요청하면
     * Then 구간을 추가할 수 없다는 에러가 발생한다
     */
    @DisplayName("[Error] 지하철 노선에 상행역, 하행역이 모두 존재하지 않는 구간을 등록")
    @Test
    void addLineSectionWithNonExistsUpStationAndDownStation() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        final Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");

        // when
        final ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역));

        // then
        요청이_정상적으로_처리되었는지_확인(지하철_노선에_지하철_구간_생성_응답, HttpStatus.BAD_REQUEST);
        에러메시지_확인(지하철_노선에_지하철_구간_생성_응답, ErrorCode.SECTION_NOT_FOUND_ABOUT_UP_AND_DOWN_STATION);
    }

    /**
     * When 지하철 노선에 상행 종점으로 새로운 구간 추가를 요청하면
     * Then 노선의 맨 처음에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 상행 종점으로 구간을 등록")
    @Test
    void addLineSectionWithUpStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        요청이_정상적으로_처리되었는지_확인(response, HttpStatus.OK);
        지하철역_순서를_검증(response, List.of(정자역, 강남역, 양재역));
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
        요청이_정상적으로_처리되었는지_확인(response, HttpStatus.OK);
        지하철역_순서를_검증(response, List.of(강남역, 양재역));
    }

    /**
     * When 지하철 노선의 마지막 남은 구간에 대한 제거를 요청하면
     * Then 구간을 제거할 수 없다는 에러가 발생한다
     */
    @DisplayName("[Error] 지하철 노선에 구간이 하나 남았을 때, 마지막 구간을 제거")
    @Test
    void removeLastLineSection() {
        // when
        final ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_응답 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        요청이_정상적으로_처리되었는지_확인(지하철_노선에_지하철_구간_제거_응답, HttpStatus.BAD_REQUEST);
        에러메시지_확인(지하철_노선에_지하철_구간_제거_응답, ErrorCode.CANNOT_REMOVE_LAST_SECTION);
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private void 지하철역_순서를_검증(ExtractableResponse<Response> response, List<Long> stationIdList) {
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsAnyElementsOf(stationIdList);
    }
}
