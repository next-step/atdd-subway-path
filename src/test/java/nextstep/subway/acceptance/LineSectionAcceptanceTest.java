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

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {

    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 신규역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     * Given 신규로 추가할 역을 생성 한다
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        신규역 = 지하철역_생성_요청("신규역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    @DisplayName("신규 구간의 하행역이 기존 구간의 상행 종점역과 동일할 경우 상행종점역 으로 구간 추가하기 ")
    @Test
    public void add_up_station_at_line_front() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신규역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_역_순서_확인(response, 신규역, 강남역, 양재역);
    }

    @DisplayName("신규 구간의 상행역이 기존 구간의 하행역과 동일할 경우 하행종점역 으로 구간 추가하기")
    @Test
    public void add_down_station_at_line_back() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 신규역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_역_순서_확인(response, 강남역, 양재역, 신규역);
    }

    @DisplayName("신규 구간의 상행역이 기존 구간의 상행역과 동일할 경우 중간에 구간을 추가할 수 있다")
    @Test
    public void add_section_at_line_middle() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 신규역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_역_순서_확인(response, 강남역, 신규역, 양재역);
    }

    @DisplayName("역 사이에 새로운 역 추가할때, 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다")
    @Test
    public void add_section_fail_by_over_distance() {
        // when
        int overDistance = 11;
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 신규역, overDistance));

        // then
        생성_실패_확인(response, HttpStatus.BAD_REQUEST, "기존의 구간 길이보다 긴 신규구간을 중간에 추가할 수 없습니다");
    }

    @DisplayName("역 사이에 새로운 역 추가할때, 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    public void add_section_fail_by_already_register_up_down_stations() {
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 3));

        // then
        생성_실패_확인(response, HttpStatus.BAD_REQUEST, "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다");
    }

    @DisplayName("역 사이에 새로운 역 추가할때, 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    public void add_section_fail_by_not_exist_up_down_stations() {
        // given
        Long 없는역1 = 지하철역_생성_요청("없는역1").jsonPath().getLong("id");
        Long 없는역2 = 지하철역_생성_요청("없는역2").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(없는역1, 없는역2, 3));

        // then
        생성_실패_확인(response, HttpStatus.NOT_FOUND, "상행역과 하행역 모두 찾을 수 없습니다");
    }

    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_역_순서_확인(response, 강남역, 양재역, 정자역);
    }

    /**
     * When 상행 종점역을 상행역으로 하는 구간에 삭제 요청시
     * Then 구간 삭제가 성공하고
     * Then 역 목록을 응답 받는다
     */
    @DisplayName("상행 종점역을 삭제할 수 있다.")
    @Test
    public void remove_up_station() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 신규역, 3));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_역_순서_확인(response, 양재역, 신규역);
    }

    /**
     * When 하행 종점역을 하행역으로 하는 구간에 삭제 요청시
     * Then 구간 삭제가 성공하고
     * Then 역 목록을 응답 받는다
     */
    @DisplayName("하행 종점역을 삭제할 수 있다.")
    @Test
    public void remove_down_station() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 신규역, 3));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 신규역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_역_순서_확인(response, 강남역, 양재역);
    }

    /**
     * When 두 구간의 중간역을 삭제 요청시
     * Then 구간 삭제가 성공하고
     * Then 역 목록을 응답 받는다
     */
    @DisplayName("중간역을 삭제할 수 있다")
    @Test
    public void remove_middle_station() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 신규역, 3));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_역_순서_확인(response, 강남역, 신규역);
    }

    /**
     * When 상행종점역과 하행종점역을 제외한 모든 역을 삭제요청하
     * When 상행종점역을 삭제 요청시
     * Then 구간 삭제가 실패한
     */
    @DisplayName("구간이 하나인 경우, 해당 마지막 구간은 제거할 수 없다")
    @Test
    public void remove_last_section_fail() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        생성_실패_확인(response, HttpStatus.BAD_REQUEST, "노선의 마지막 하나 남은 구간은 삭제할 수 없습니다");

    }

    /**
     * When 등록되지 않은 역에 삭제 요청시
     * Then 구간 삭제가 실패한다
     */
    @DisplayName("등록되지 않은 역은 삭제할 수 없다")
    @Test
    public void remove_not_exists_section_fail() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 신규역, 3));
        Long 노선에등록되지않은역 = 지하철역_생성_요청("노선에등록되지않은역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 노선에등록되지않은역);

        // then
        생성_실패_확인(response, HttpStatus.NOT_FOUND, "해당 역을 찾을 수 없어 구간을 삭제할 수 없습니다");
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
        return createSectionCreateParams(upStationId, downStationId, 6);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance.toString());
        return params;
    }
}
