package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.exception.message.SectionErrorMessage;
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
    private Long 정자역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = 라인_파라미터_생성(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void 지하철_노선에_구간을_등록() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, 구간_파라미터_생성(양재역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선 중간에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @Test
    void 지하철_노선_중간에_구간_등록() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, 구간_파라미터_생성(강남역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 지하철 노선 처음에 새로운 구음 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @Test
    void 지하철_노선_처음에_구간_등록() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, 구간_파라미터_생성(정자역, 강남역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선 중간에 새로운 구간 추가를 요청
     * When 요청한 구간의 길이가 추가될 노선의 길이와 같으면
     * Then 에러 발생
     */
    @Test
    void 지하철_노선_중간_등록_길이가_같은_경우_에러() {
        // when

        // then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 구간_파라미터_생성(강남역, 정자역, 10));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo(SectionErrorMessage.SECTION_DISTANCE_EQUALS_OR_LARGE);
    }

    /**
     * Given 지하철 노선 중간에 새로운 구간 추가를 요청 후 다시 새로운 구간 추가를 요청
     * When 요청한 구간의 길이가 추가될 노선의 길이와 같으면
     * Then 에러 발생
     */
    @Test
    void 지하철_노선_중간_등록_후_새로운_구간_등록_길이가_같은_경우_에러() {
        // when
        Long 모란역 = 지하철역_생성_요청("모란역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, 구간_파라미터_생성(강남역, 정자역, 5));

        // then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 구간_파라미터_생성(정자역, 모란역, 5));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo(SectionErrorMessage.SECTION_DISTANCE_EQUALS_OR_LARGE);
    }

    /**
     * Given 지하철 노선 중간에 새로운 구간 추가를 요청
     * When 요청한 구간의 길이가 추가될 노선의 길이보다 크면
     * Then 에러 발생
     */
    @Test
    void 지하철_노선_중간_등록_길이가_큰_경우_에러() {
        // when

        // then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 구간_파라미터_생성(강남역, 정자역, 15));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo(SectionErrorMessage.SECTION_DISTANCE_EQUALS_OR_LARGE);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청
     * When 요청한 구간 상행, 하행역이 기존 노선에 모두 등록 되어있다면
     * Then 에러 발생
     */
    @Test
    void 지하철_노선_등록할_구간_상행역_하행역_모두_있는_경우_에러() {
        // then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 구간_파라미터_생성(강남역, 양재역, 6));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo(SectionErrorMessage.SECTION_DUPLICATION);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청
     * When 요청한 구간 상행, 하행역이 기존 노선에 모두 없다면
     * Then 에러 발생
     */
    @Test
    void 지하철_노선_등록할_구간_상행역_하행역_모두_없는_경우_에러() {
        // when
        Long 모란역 = 지하철역_생성_요청("모란역").jsonPath().getLong("id");

        // then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 구간_파라미터_생성(모란역, 정자역, 6));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo(SectionErrorMessage.SECTION_NOT_IN_STATION);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void 지하철_노선에_구간을_제거() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, 구간_파라미터_생성(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    private Map<String, String> 라인_파라미터_생성(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    private Map<String, String> 구간_파라미터_생성(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}