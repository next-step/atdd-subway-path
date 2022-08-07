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
        Integer distance = 5;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, distance));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선 중간에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 중간에 역 뒤로 구간을 등록")
    @Test
    void addLineInMiddleBehindSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        Long 양재시민의숲역 = 지하철역_생성_요청("양재시민의숲역").jsonPath().getLong("id");

        //when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재시민의숲역, 정자역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 양재시민의숲역, 정자역);
    }

    /**
     * When 지하철 노선 중간에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 중간에 역 앞으로 구간을 등록")
    @Test
    void addLineInMiddleFrontSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");

        //when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역, 미금역);
    }

    /**
     * When 지하철 노선 중간에 해당 노선 길이 이상인 새로운 구간 추가를 요청 하면
     * Then 400에러가 발생한다
     */
    @DisplayName("지하철 노선 중간에 길이 이상인 구간을 등록 실패")
    @Test
    void fail_addLongLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 5));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선 중간에 상행,하행역이 이미 노선에 존재하는 구간 추가를 요청 하면
     * Then 400에러가 발생한다
     */
    @DisplayName("지하철 노선 중간에 이미 등록된 역들을 가진 구간 등록 실패")
    @Test
    void fail_addLineSectionWithAlreadyExistStations() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역, 5));

        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 미금역, 5));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선 중간에 상행,하행역이 모두 노선에 존재하지 않는 구간 추가를 요청 하면
     * Then 400에러가 발생한다
     */
    @DisplayName("지하철 노선 중간에 모두 등록되지 않은 역을 가진 구간 등록 실패")
    @Test
    void fail_addLineSectionWithNotExistStations() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");
        Long 청계산입구역 = 지하철역_생성_요청("청계산입구역").jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(청계산입구역, 미금역, 3));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 역 제거를 요청 하면
     * Then 노선에 해당 역이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 청계산입구역 = 지하철역_생성_요청("청계산입구역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(청계산입구역, 정자역, 3));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 청계산입구역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 유일한 구간 제거를 요청 하면
     * Then 400에러가 발생한다.
     */
    @DisplayName("역 하나뿐인 지하철 노선에 구간을 제거 실패")
    @Test
    void fail_removeLineWithOneSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선에 존재하지 않는 역 제거를 요청 하면
     * Then 400에러가 발생한다
     */
    @DisplayName("지하철 노선에 존재하지 않는 역을 제거 실패")
    @Test
    void fail_removeLineWithNoStation() {
        // when
        Long 사당역 = 지하철역_생성_요청("사당역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 사당역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
