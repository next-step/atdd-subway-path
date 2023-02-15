package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
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
import static nextstep.subway.common.error.SubwayError.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 역삼역;
    private Long 강남역;
    private Long 양재역;
    private Long 몽촌토성역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        몽촌토성역 = 지하철역_생성_요청("몽촌토성역").jsonPath().getLong("id");

        Map<String, Object> lineCreateParams = createLineCreateParams("신분당선", "bg-red-600", 강남역, 양재역, 10);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선 기존 구간에 신규 구간의 새로운 역을 상행 종점으로 등록하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addUpSection() {
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(역삼역, 강남역, 4));

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(역삼역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선 기존 구간에 신규 구간의 새로운 역을 하행 종점으로 등록하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addDownSection() {
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 몽촌토성역, 4));

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 몽촌토성역);
    }

    /**
     * When 지하철 노선 기존 구간에 요청한 상행역과 하행역이 이미 노선에 등록되어 있으면
     * Then 노선에 새로운 구간 추가가 불가하다
     */
    @DisplayName("요청한 상행역과 하행역이 이미 노선에 등록되어 있어서 추가가 불가하다.")
    @Test
    void error_addSection() {
        ExtractableResponse<Response> 지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 4));

        final JsonPath jsonPathResponse = 지하철_구간_생성_응답.response().body().jsonPath();
        assertAll(
                () -> assertThat(지하철_구간_생성_응답.response().statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(jsonPathResponse.getString("message")).isEqualTo(NO_REGISTER_EXIST_STATION.getMessage())
        );
    }

    /**
     * When 지하철 노선 기존 구간에 요청한 상행역과 하행역 모두 등록되어 있지 않으면
     * Then 노선에 새로운 구간 추가가 불가하다
     */
    @DisplayName("요청한 상행역과 하행역 모두 노선에 등록되어 있지 않아서 추가가 불가하다.")
    @Test
    void error_addSection_2() {
        final ExtractableResponse<Response> 지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(역삼역, 몽촌토성역, 4));

        final JsonPath jsonPathResponse = 지하철_구간_생성_응답.response().body().jsonPath();
        assertAll(
                () -> assertThat(지하철_구간_생성_응답.response().statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(jsonPathResponse.getString("message")).isEqualTo(NO_REGISTER_NO_EXIST_STATION.getMessage())
        );
    }

    /**
     * When 지하철 노선 기존 구간 역 사이에 새로운 역의 길이가 기존 역 사이 길이보다 크거나 같으면
     * Then 노선에 새로운 구간 추가가 불가하다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void error_addSection_3() {
        final ExtractableResponse<Response> 지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 몽촌토성역, 10));

        final JsonPath jsonPathResponse = 지하철_구간_생성_응답.response().body().jsonPath();
        assertAll(
                () -> assertThat(지하철_구간_생성_응답.response().statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(jsonPathResponse.getString("message")).isEqualTo(NO_REGISTER_DISTANCE_GREATER_THAN.getMessage())
        );
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 상행종점역을 구간 삭제 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 상행종점역 제거")
    @Test
    void removeLineUpStation() {
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 몽촌토성역, 4));

        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 몽촌토성역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 중간역을 구간 삭제 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 중간역 제거")
    @Test
    void removeLineMiddleStation() {
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 몽촌토성역, 4));

        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 몽촌토성역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 상행종점역을 구간 삭제 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 하행종점역 제거")
    @Test
    void removeLineDownStation() {
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 몽촌토성역, 4));

        지하철_노선에_지하철_구간_제거_요청(신분당선, 몽촌토성역);

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 구간이 하나인 노선에서 마지막 구간 제거 요청하면
     * Then 구간 제거가 불가하다.
     */
    @DisplayName("노선이 하나인 경우 제거가 불가능하다")
    @Test
    void error_removeLineStation() {
        final ExtractableResponse<Response> 지하철_구간_제거_응답 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        final JsonPath jsonPathResponse = 지하철_구간_제거_응답.response().body().jsonPath();
        assertAll(
                () -> assertThat(지하철_구간_제거_응답.response().statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(jsonPathResponse.getString("message")).isEqualTo(NO_DELETE_ONE_SECTION.getMessage())
        );
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 노선에 등록되지 않은 역을 제거 요청하면
     * Then 등록되지 않는 역을 제거가 불가하다.
     */
    @DisplayName("등록되지 않는 역은 제거가 불가하다.")
    @Test
    void error_removeLineStation_2() {
        final ExtractableResponse<Response> 지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 몽촌토성역, 10));

        final ExtractableResponse<Response> 지하철_구간_제거_응답 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 역삼역);

        final JsonPath jsonPathResponse = 지하철_구간_제거_응답.response().body().jsonPath();
        assertAll(
                () -> assertThat(지하철_구간_제거_응답.response().statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(jsonPathResponse.getString("message")).isEqualTo(NO_REMOVE_NOT_REGISTER_LINE_STATION.getMessage())
        );
    }

    private Map<String, Object> createLineCreateParams(final String name, final String color, final Long upStationId, final Long downStationId, final Integer distance) {
        Map<String, Object> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId);
        lineCreateParams.put("downStationId", downStationId);
        lineCreateParams.put("distance", distance);
        return lineCreateParams;
    }

    private Map<String, Object> createSectionCreateParams(final Long upStationId, final Long downStationId, final Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }
}
