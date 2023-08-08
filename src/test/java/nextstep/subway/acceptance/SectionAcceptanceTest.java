package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.constants.ErrorMessage;
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청할 때 구간이 있으면
     * Then 노선이 추가되지 않는다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 실패 (이미 지정된 구간)")
    @Test
    void addLineSectionForSame() {

        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 강남역, 10));
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.SECTION_ALREADY_EXISTED);
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청할 때 등록할 구간이 기존보다 길면
     * Then 노선이 추가되지 않는다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 실패 (길이 에러)")
    @Test
    void addLineSectionAlreadyExist() {

        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 11));

        assertThat(response.body().asString()).isEqualTo(ErrorMessage.LENGTH_ERROR);
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청할 때 상행역, 하행역이 둘 다 없으면
     * Then 노선이 추가되지 않는다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 상행역 / 하행역 둘다 없음")
    @Test
    void addLineSectionNotExist() {

        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 명동역 = 지하철역_생성_요청("명동역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 명동역, 11));

        assertThat(response.body().asString()).isEqualTo(ErrorMessage.CONCLUED_UP_OR_DOWN);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 마지막 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);

    }

    /**
     * Given 지하철 노선에 구간 추가를 하고
     * When 지하철 노선의 가운데 있는 역을 제거하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 가운데 역을 제거")
    @Test
    void removeLineSectionMiddle() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    /**
     * Given 지하철 노선에 1개의 구간이 있을 때
     * When 지하철 노선의 구간을 제거하면
     * Then 구간을 제거할 수 없다
     */
    @DisplayName("지하철 노선 구간을 제거 - 실패 (구간이 하나)")
    @Test
    void removeLineSectionByOne() {

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.SECTION_MORE_THAN_TWO);
    }

    /**
     * Given 지하철 노선에 2개의 구간이 있을 때
     * When 지하철 노선에 존재하지 않는 역이 포함된 구간을 제거하면
     * Then 구간을 제거할 수 없다
     */
    @DisplayName("지하철 노선 구간을 제거 - 실패 (노선에 등록되지 않은 역이 있음)")
    @Test
    void removeLineSectionNoSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));
        Long 범계역 = 지하철역_생성_요청("범계").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 범계역);

        // then
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.STATION_IS_NOT_SELECTED);
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
