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
    private Long 논현역;
    private Long 신논현역;

    private Long 양재시민의숲;
    private Long 양재전역;


    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        양재시민의숲 = 지하철역_생성_요청("양재시민의숲").jsonPath().getLong("id");
        양재전역 = 지하철역_생성_요청("양재전역").jsonPath().getLong("id");

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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 기존 지하철 구간 사이에 들어가는 구간을 생성하고
     * When 지하철 노선에 지하철 구간 생성을 요청하면
     * Then 두 개의 구간으로 나뉘어 생성된다.
     */
    @DisplayName("지하철역 사이에 새로운 역을 등록")
    @Test
    void 지하철역_사이에_새로운_역을_등록() {
//        Given
//        When
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 강남역, 6));
//        Then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(논현역, 강남역, 양재역);
    }

    /**
     * Given 노선의 상행역과 같은 하행역 구간을 생성하고
     * When 지하철 노선에 지하철 구간 생성을 요청하면
     * Then 새로운 역이 상행 종점으로 등록된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void 상행_종점_등록() {
//        Given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 6));
//        When
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
//        When
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신논현역, 강남역, 양재역);
    }

    /**
     * Given 노선의 하행역과 같은 상행역 구간을 생성하고
     * When 지하철 노선에 지하철 구간 생성을 요청하면
     * Then 새로운 역이 하행 종점으로 등록된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void 하행_종점_등록() {
//        Given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재시민의숲, 6));
//        When
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
//        Then
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 양재시민의숲);
    }

    /**
     * Given 기존 역 사이의 길이보다 긴 역 사이에 새로운 역을 생성하고
     * When 지하철 노선에 지하철 구간 생성을 요청하면
     * Then 구간 생성에 실패한다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void 구간_사이_구간_등록_시_길이_조건_테스트() {
//        When
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재전역, 11));
//        Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("추가하려는 구간의 길이가 기존 길이보다 같거나 길 수 없습니다.");
    }

    /**
     * Given 상행역과 하행역이 이미 노선에 등록되어 있는 구간을 생성하고 (A-B, B-C)인 노선에 A-C추가
     * When 구간 생성을 요청하면
     * Then 구간 등록에 실패한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음(A-B, B-C)인 노선에 A-C추가")
    @Test
    void 노선에_등록된_상행역_하행역_구간_등록_테스트_AB_BC_구간_AC추가() {
//        Given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 5));
//        When
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 양재역, 2));
//        Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("최대 1개의 역만 노선에 등록되어 있어야 합니다.");
    }

    /**
     * Given 상행역과 하행역이 이미 노선에 등록되어 있는 구간을 생성하고 (A-B, B-C)인 노선에 B-C추가
     * When 구간 생성을 요청하면
     * Then 구간 등록에 실패한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음(A-B, B-C)인 노선에 A-B추가")
    @Test
    void 노선에_존재하지_않는_상행역_하행역_구간_등록_AB_BC_AB구간추가() {
//        Given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 5));
//        When
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 2));
//        Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("최대 1개의 역만 노선에 등록되어 있어야 합니다.");
    }

    /**
     * Given 상행역과 하행역이 모두 노선에 포함되지 않은 노선을 생성하고
     * When 구간 생성을 요청하면
     * Then 구간 등록에 실패한다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
//        Given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 5));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 5));
//        When
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 양재전역, 2));
//        Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("최소 1개 이상의 역은 노선에 등록되어 있어야 합니다.");
    }

    /**
     * Given 구간이 하나의 노선을 생성한다.
     * When 구간 삭제를 한다.
     * Then 구간 삭제에 실패를 한다.
     */
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거 불가")
    @Test
    void 하나인_구간_삭제_실패() {

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("두 개 이상의 구간일때만 삭제가 가능합니다.");
    }

    /**
     * Given 2개 이상의 구간을 가지는 노선을 생성한다.
     * When 중간역 삭제를 한다.
     * Then 중간역 삭제에 성공한다.
     */
    @DisplayName("중간역 삭제")
    @Test
    void 중간역_삭제() {

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 5));

        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신논현역, 양재역);
    }

    /**
     * Given 2개 이상의 구간을 가지는 노선을 생성한다.
     * When 상행역 삭제를 한다.
     * Then 상행역 삭제에 성공한다.
     */
    @DisplayName("상행역 삭제")
    @Test
    void 상행역_삭제() {

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 5));

        지하철_노선에_지하철_구간_제거_요청(신분당선, 신논현역);

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
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
