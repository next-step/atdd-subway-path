package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.SectionSteps.createLineCreateParams;
import static nextstep.subway.acceptance.SectionSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선_ID;
    private Long 강남역_ID;
    private Long 양재역_ID;
    private Long 판교역_ID;
    private Long 정자역_ID;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역_ID = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역_ID = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        판교역_ID = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        정자역_ID = 지하철역_생성_요청("정자역").jsonPath().getLong("id");


        Map<String, String> lineCreateParams = createLineCreateParams(강남역_ID, 양재역_ID, 10);
        신분당선_ID = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        Long 정자역_ID = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선_ID, createSectionCreateParams(양재역_ID, 정자역_ID, 10));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선_ID);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_ID, 양재역_ID, 정자역_ID);
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
        Long 정자역_ID = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선_ID, createSectionCreateParams(양재역_ID, 정자역_ID, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선_ID, 정자역_ID);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선_ID);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_ID, 양재역_ID);
    }

    /**
     * Given 노선을 생성한 후,
     * When 하행역을 추가하고
     * When 상행역을 추가해도
     * Then 목록은 순서대로 조회할 수 있다.
     */
    @Test
    void addLineSection1() {
        // given 노선을 생성한 후
        Map<String, String> 강남역_양재역 = createLineCreateParams(강남역_ID, 양재역_ID, 10);
        ExtractableResponse<Response> 신분당선_생성_요청 = 지하철_노선_생성_요청(강남역_양재역);

        assertThat(신분당선_생성_요청.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when 하행역을 추가하고
        Long 신분당선_ID = 신분당선_생성_요청.jsonPath().getLong("id");
        Long 정자역_ID = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Map<String, String> 양재역_정자역 = createSectionCreateParams(양재역_ID, 정자역_ID, 10);
        ExtractableResponse<Response> 하행_구간_생성_요청 = 지하철_노선에_지하철_구간_생성_요청(신분당선_ID, 양재역_정자역);

        assertThat(하행_구간_생성_요청.statusCode()).isEqualTo(HttpStatus.OK.value());

        // when 상행역을 추가해도
        Map<String, String> 판교역_강남역 = createSectionCreateParams(판교역_ID, 강남역_ID, 6);
        ExtractableResponse<Response> 상행_구간_생성_요청 = 지하철_노선에_지하철_구간_생성_요청(신분당선_ID, 판교역_강남역);

        assertThat(상행_구간_생성_요청.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then 목록은 순서대로 조회할 수 있다.
        ExtractableResponse<Response> 지하철_노선_조회 = 지하철_노선_조회_요청(신분당선_ID);

        assertThat(지하철_노선_조회.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_조회.body().jsonPath().getList("stations.name")).containsExactly("판교역", "강남역", "양재역", "정자역");
    }

    /**
     * Given 노선을 생성한 후,
     * When 하행역을 추가하고
     * When 중간역을 추가해도
     * Then 목록은 순서대로 조회할 수 있다.
     */
    @Test
    void addLineSection2() {
        // given 노선을 생성한 후
        Map<String, String> 강남역_양재역 = createLineCreateParams(강남역_ID, 양재역_ID, 10);
        ExtractableResponse<Response> 신분당선_생성_요청 = 지하철_노선_생성_요청(강남역_양재역);
        assertThat(신분당선_생성_요청.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when 하행역을 추가하고
        Long 신분당선_ID = 신분당선_생성_요청.jsonPath().getLong("id");
        Map<String, String> 양재역_정자역 = createSectionCreateParams(양재역_ID, 정자역_ID, 10);
        ExtractableResponse<Response> 하행_구간_생성_요청 = 지하철_노선에_지하철_구간_생성_요청(신분당선_ID, 양재역_정자역);

        assertThat(하행_구간_생성_요청.statusCode()).isEqualTo(HttpStatus.OK.value());

        // when 중간역을 추가해도
        Map<String, String> 강남역_판교역 = createSectionCreateParams(강남역_ID, 판교역_ID, 6);
        ExtractableResponse<Response> 중간_구간_생성_요청 = 지하철_노선에_지하철_구간_생성_요청(신분당선_ID, 강남역_판교역);

        assertThat(중간_구간_생성_요청.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then 목록은 순서대로 조회할 수 있다.
        ExtractableResponse<Response> 지하철_노선_조회 = 지하철_노선_조회_요청(신분당선_ID);

        assertThat(지하철_노선_조회.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_조회.body().jsonPath().getList("stations.name")).containsExactly("강남역", "판교역", "양재역", "정자역");
    }

    /**
     * Given 노선을 생성한 후,
     * When 상행역을 추가하고
     * When 중간역을 추가해도
     * Then 목록은 순서대로 조회할 수 있다.
     */
    @Test
    void addLineSection3() {
        // given 노선을 생성한 후
        Map<String, String> 강남역_양재역 = createLineCreateParams(강남역_ID, 양재역_ID, 10);
        ExtractableResponse<Response> 신분당선_생성_요청 = 지하철_노선_생성_요청(강남역_양재역);
        assertThat(신분당선_생성_요청.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when 상행역을 추가하고
        Long 신분당선_ID = 신분당선_생성_요청.jsonPath().getLong("id");
        Map<String, String> 판교역_강남역 = createSectionCreateParams(판교역_ID, 강남역_ID, 6);
        ExtractableResponse<Response> 상행_구간_생성_요청 = 지하철_노선에_지하철_구간_생성_요청(신분당선_ID, 판교역_강남역);

        assertThat(상행_구간_생성_요청.statusCode()).isEqualTo(HttpStatus.OK.value());

        // when 중간역을 추가해도
        Map<String, String> 정자역_강남역 = createSectionCreateParams(정자역_ID, 강남역_ID, 2);
        ExtractableResponse<Response> 중간_구간_생성_요청 = 지하철_노선에_지하철_구간_생성_요청(신분당선_ID, 정자역_강남역);

        assertThat(중간_구간_생성_요청.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        ExtractableResponse<Response> 지하철_노선_조회 = 지하철_노선_조회_요청(신분당선_ID);

        assertThat(지하철_노선_조회.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_조회.body().jsonPath().getList("stations.name")).containsExactly("판교역", "정자역", "강남역", "양재역");
    }


}
