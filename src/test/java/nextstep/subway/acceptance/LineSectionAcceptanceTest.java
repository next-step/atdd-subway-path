package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

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

        LineRequest lineCreateParams = createLineCreateParams(강남역, 양재역);
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

        지하철_노선에_지하철역이_포함됐는지_확인한다(response, 강남역, 양재역, 정자역);
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

        지하철_노선에_지하철역이_포함됐는지_확인한다(response, 강남역, 양재역);
    }

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     * Given 구간 추가를 요청하고 (A-C)
     * When 지하철 노선에 상행역을 기준으로 구간을 추가하면 (A-B)
     * Then 노선에 새로운 구간이 추가된다. (A-B-C)
     */
    @DisplayName("기존 구간의 역을 기준으로 새로운 구간을 추가")
    @Test
    void addExistSectionAddSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 7));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        지하철_노선에_지하철역이_포함됐는지_확인한다(response, 강남역, 정자역, 양재역);
    }

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     * Given 구간 추가를 요청하고 (A-C)
     * When 지하철 노선에 상행역을 기준으로 구간을 추가하면 (B-A)
     * Then 노선에 새로운 구간이 추가된다. (B-A-C)
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addUpStationLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 7));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        지하철_노선에_지하철역이_포함됐는지_확인한다(response, 정자역, 강남역, 양재역);
    }

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     * Given 구간 추가를 요청하고 (A-C)
     * When 지하철 노선에 상행역을 기준으로 구간을 추가하면 (C-B)
     * Then 노선에 새로운 구간이 추가된다. (A-C-B)
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addDownStationLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 7));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        지하철_노선에_지하철역이_포함됐는지_확인한다(response, 강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     * Given 구간 추가를 요청하고
     * When 중간역을 제거하면
     * Then 구간이 삭제되고 구간이 재배치 된다.
     */
    @DisplayName("중간역이 제거될 경우 구간을 재배치 한다")
    @Test
    void rearrangeSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 7));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 판교역, 5));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역이_포함됐는지_확인한다(response, 강남역, 양재역, 판교역);
    }

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     * Given 구간 추가를 요청하고
     * When 구간에 포함되지 않은 역을 삭제하면
     * Then 에러가 발생한다.
     */
    @DisplayName("구간에 포함되지 않은 역을 삭제하면 에러가 발생한다")
    @Test
    void notValidStationSectionDeleteRequest() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 7));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 판교역);

        // then
        인수테스트_요청_응답을_확인한다(response, HttpStatus.BAD_REQUEST);
    }


    private LineRequest createLineCreateParams(Long upStationId, Long downStationId) {
        return new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
    }
}
