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
    private Long 서울역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        서울역 = 지하철역_생성_요청("서울역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(서울역, 강남역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");

        Map<String, String> lineCreateParams2 = createLineCreateParams(강남역, 양재역);
        지하철_노선에_지하철_구간_생성_요청(신분당선, lineCreateParams2);
    }

    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        int 구간거리 = 10;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 구간거리));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선_조회_요청_응답(response, 서울역, 강남역, 양재역, 정자역);
    }

    @DisplayName("지하철 노선에 새로운 첫번째 구간을 등록")
    @Test
    void 첫번째_구간_등록() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        int 구간거리 = 10;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 서울역, 구간거리));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선_조회_요청_응답(response, 정자역, 서울역, 강남역, 양재역);
    }

    @DisplayName("지하철 노선에 새로운 상행 기준 두번째 구간을 등록")
    @Test
    void 상행_기준_두번째_구간_등록() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        int 구간거리 = 3;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(서울역, 정자역, 구간거리));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선_조회_요청_응답(response, 서울역, 정자역, 강남역, 양재역);
    }

    @DisplayName("지하철 노선에 새로운 하행 기준 두번째 구간을 등록")
    @Test
    void 하행_기준_두번째_구간_등록() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        int 구간거리 = 3;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 구간거리));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선_조회_요청_응답(response, 서울역, 정자역, 강남역, 양재역);
    }

    @DisplayName("지하철 노선에 새로운 두번째 구간을 상행 기준으로 등록하지만 기존 구간보다 거리가 길어 구간 등록이 실패")
    @Test
    void 상행_기준_두번째_구간_등록_실패() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        int 구간거리 = 13;
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청 =
                지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(서울역, 정자역, 구간거리));

        // then
        지하철_노선_지하철_구간_생성_요청_응답(지하철_노선에_지하철_구간_생성_요청);
    }

    @DisplayName("지하철 노선에 새로운 두번째 구간을 하행 기준으로 등록하지만 기존 구간보다 거리가 길어 구간 등록이 실패")
    @Test
    void 하행_기준_두번째_구간_등록_실패() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        int 구간거리 = 13;
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청 =
                지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 구간거리));

        // then
        지하철_노선_지하철_구간_생성_요청_응답(지하철_노선에_지하철_구간_생성_요청);
    }

    @DisplayName("지하철 노선에 존재하지 않는 상행역과 하행역을 가진 구간을 등록하는 경우 등록이 실패")
    @Test
    void 지하철_노선에_존재하지_않는_상행역과_하행역_등록_실패() {
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 구로역 = 지하철역_생성_요청("구로역").jsonPath().getLong("id");
        int 구간거리 = 13;
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청 =
                지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 구로역, 구간거리));

        // then
        지하철_노선_지하철_구간_생성_요청_응답(지하철_노선에_지하철_구간_생성_요청);
    }

    @DisplayName("지하철 노선에 이미 노선에 등록된 상행역과 하행역을 가진 구간을 등록하는 경우 등록이 실패")
    @Test
    void 지하철_노선에_등록되어_있는_상행역과_하행역_등록_실패() {
        int 구간거리 = 1;
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청 =
                지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(서울역, 양재역, 구간거리));

        // then
        지하철_노선_지하철_구간_생성_요청_응답(지하철_노선에_지하철_구간_생성_요청);
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
        int 구간거리 = 10;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 구간거리));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선_조회_요청_응답(response, 서울역, 강남역 ,양재역);
    }

    @DisplayName("지하철 노선에 첫번째 역을 제거")
    @Test
    void 첫번째역을_제거() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        int 구간거리 = 4;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(서울역, 정자역, 구간거리));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 서울역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선_조회_요청_응답(response, 정자역, 강남역 ,양재역);
    }
    
    @DisplayName("지하철 노선에 두번째 역을 제거")
    @Test
    void 두번째역을_제거() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        int 구간거리 = 4;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(서울역, 정자역, 구간거리));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선_조회_요청_응답(response, 서울역, 강남역 ,양재역);
    }

    @DisplayName("지하철 노선에 존재하지 않는역을 제거")
    @Test
    void 존재하지_않는역을_제거() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        지하철_노선_지하철역_삭제_요청_응답(response);
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
}
