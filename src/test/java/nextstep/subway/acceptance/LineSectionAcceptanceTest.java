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
import static nextstep.subway.common.AddTypeEnum.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;
    private Long 양재역;
    private Long 정자역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(양재역, 정자역);
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
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_New_요청(BACK_ADD_SECTION, 신분당선, createSectionCreateParams(정자역, 미금역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역, 미금역);
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 동일한 상행역 구간 앞에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 (앞에 추가)")
    @Test
    void addLineSection_front() {
        // when
        Long 강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_New_요청(FRONT_ADD_SECTION, 신분당선, createSectionCreateParams(강남역, 양재역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 동일한 상행역 구간 중간에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 (중간에 추가)")
    @Test
    void addLineSection_middle() {
        // when
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_New_요청(MIDDLE_ADD_SECTION, 신분당선, createSectionCreateParams(양재역, 판교역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 판교역, 정자역);
    }

    /**
     * when 노선에 존재하는 상행역과 하행역으로 구성된 구간을 추가 요청한다
     * then "section's stations is already all exist or no exist" 에러 메세지와 함께 에러가 발생한다
     */
    @DisplayName("지하철 노선에 구간을 등록 중 Exception 발생")
    @Test
    void addLineSection_exception() {
        // when
        var response = 지하철_노선에_지하철_구간_생성_New_요청(MIDDLE_ADD_SECTION, 신분당선, createSectionCreateParams(정자역, 양재역));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * given 노선에 없는 역 2개를 생성한다.
     * when 노선에 없는 상행역과 하행역으로 구성된 구간을 추가 요청한다
     * then "section's stations is already all exist or no exist" 에러 메세지와 함께 에러가 발생한다
     */
    @DisplayName("지하철 노선에 구간을 등록 중 Exception 발생")
    @Test
    void addLineSection_exception2() {
        //given
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");

        // when
        var response = 지하철_노선에_지하철_구간_생성_New_요청(MIDDLE_ADD_SECTION, 신분당선, createSectionCreateParams(판교역, 미금역));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

//    /**
//     * when 노선에 중간에 추가하려는 구간의 길이와 동일한 새로운 구간을 추가 요청한다
//     * then "section's stations is already all exist or no exist" 에러 메세지와 함께 에러가 발생한다
//     */
//    @DisplayName("지하철 노선에 구간을 등록 중 Exception 발생")
//    @Test
//    void addLineSection_middle_exception() {
//        //given
//        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
//
//        // when
//        Map<String, String> params = new HashMap<>();
//        params.put("upStationId", 양재역 + "");
//        params.put("downStationId", 판교역 + "");
//        params.put("distance", 10 + "");
//
//        var response = 지하철_노선에_지하철_구간_생성_New_요청(MIDDLE_ADD_SECTION, 신분당선, createSectionCreateParams(양재역, 판교역));
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
//    }

//    /**
//     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
//     * When 지하철 노선의 마지막 구간 제거를 요청 하면
//     * Then 노선에 구간이 제거된다
//     */
//    @DisplayName("지하철 노선에 구간을 제거")
//    @Test
//    void removeLineSection() {
//        // given
//        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");
//        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역));
//
//        // when
//        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);
//
//        // then
//        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 미금역);
//    }

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
