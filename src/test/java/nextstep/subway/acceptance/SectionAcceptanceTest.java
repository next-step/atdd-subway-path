package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성;
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

        강남역 = 지하철역_생성("강남역").getId();
        양재역 = 지하철역_생성("양재역").getId();

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, 10);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선의 맨 뒤에 구간 등록을 요청 하면
     * Then 노선의 맨 뒤에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선의 맨 뒤에 구간을 등록")
    @Test
    void 지하철_노선에_구간_추가_노선의_맨_뒤에_새로운_구간_추가() {
        // when
        final var 정자역 = 지하철역_생성("정자역").getId();
        지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선의 중간에 구간 등록을 요청 하면
     * Then 노선의 중간에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선의 중간에 구간을 등록")
    @Test
    void 지하철_노선에_구간_추가_노선의_중간에_새로운_구간_추가() {
        // when
        final var  정자역 = 지하철역_생성("정자역").getId();
        지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 5));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * Given 지하철 노선에 이미 존재하는 구간과 같은 역을 연결하는 구간을 만들고
     * When 구간 등록 요청하면
     * Then 노선의 중간에 새로운 구간이 추가된다
     */
    @DisplayName("노선에 속한 역에 새로운 구간의 상행역과 하행역이 모두 포함되는 중복 구간을 추가할 수 없다.")
    @Test
    void 지하철_노선에_구간_추가_실패_중복_구간() {
        // given
        final var 첫번째역 = StationSteps.지하철역_생성("강남역");
        final var 두번째역 = StationSteps.지하철역_생성("논현역");

        final var 노선 = LineSteps.지하철_노선_생성("신분당선", "빨강", 첫번째역.getId(), 두번째역.getId(), 10);

        // when
        final var params = Map.of(
            "upStationId", 첫번째역.getId(),
            "downStationId", 두번째역.getId(),
            "distance", 5
        );

        var response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines/{lineId}/sections", 노선.getId())
            .then().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("이미 노선에 속한 구간을 추가할 수 없습니다.");
    }

    /**
     * Given 지하철 노선에 속한 구간과 연결되지 않는 역을 연결하는 구간을 만들고
     * When 구간 등록 요청하면
     * Then 에러가 발생한다.
     */
    @DisplayName("지하철 노선의 기존 구간에 연결되지 않는 구간을 추가할 수 없다.")
    @Test
    void 지하철_노선에_구간_추가_실패_연결되지_않는_구간() {
        // given
        final var 첫번째역 = StationSteps.지하철역_생성("강남역");
        final var 두번째역 = StationSteps.지하철역_생성("논현역");

        final var 노선 = LineSteps.지하철_노선_생성("신분당선", "빨강", 첫번째역.getId(), 두번째역.getId(), 10);

        final var 세번째역 = StationSteps.지하철역_생성("정자역");
        final var 네번째역 = StationSteps.지하철역_생성("판교역");

        // when
        final var params = Map.of(
            "upStationId", 세번째역.getId(),
            "downStationId", 네번째역.getId(),
            "distance", 10
        );

        var response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines/{lineId}/sections", 노선.getId())
            .then().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("노선에 새로운 구간과 이어지는 역이 없습니다.");
    }

    /**
     * When 지하철 노선의 중간에 구간 등록을 요청 할 때 새 구간의 길이가 분할 될 구간보다 길면
     * Then 에러가 발생한다.
     */
    @DisplayName("지하철 노선의 새 구간의 거리가 분할 될 구간보다 길면 에러가 발생한다.")
    @Test
    void 지하철_노선에_구간_추가_실패_구간_길이_검증_실패() {
        // given
        final var 정자역 = 지하철역_생성("정자역").getId();

        // when
        final var response = 지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("새 구간이 추가 될 구간의 길이가 새 구간보다 짧습니다.");
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void 지하철_노선에_구간_제거_마지막_구간_제거() {
        // given
        Long 정자역 = 지하철역_생성("정자역").getId();
        지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        // when
        지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void 지하철_노선에_구간_제거_중간_구간_제거() {
        // given
        Long 정자역 = 지하철역_생성("정자역").getId();
        Long 광교역 = 지하철역_생성("광교역").getId();
        지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));
        지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 광교역, 10));

        // when
        지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 광교역);
    }

    /**
     * Given 구간이 하나인 지하철 노선을 만들고
     * When 유일한 구간을 제거하는 요청을 하면
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선에 구간 제거 실패 노선에 속하지 않은 역")
    @Test
    void 지하철_노선에_구간_제거_실패_노선에_속하지_않은_역() {
        // given
        Long 정자역 = 지하철역_생성("정자역").getId();

        // when
        지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 구간이 하나인 지하철 노선을 만들고
     * When 유일한 구간을 제거하는 요청을 하면
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void 지하철_노선에_구간_제거_유일한_구간() {
        // when
        지하철_구간_제거_요청(신분당선, 강남역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
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
