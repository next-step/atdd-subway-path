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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 기존 구간의 역을 기준으로 새로운 구간 추가를 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 역을 기준으로 구간을 등록")
    @Test
    void addLineSectionInExistentSection() {
        // when
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 중간역, 양재역);
    }

    /**
     * When 기존 상행 종점역이 하행역인 구간 추가를 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("기존 상행 종점역이 하행역인 구간을 등록")
    @Test
    void addLineSectionAsFirst() {
        // when
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신논현역, 강남역, 양재역);
    }

    /**
     * When 기존 하행 종점역이 상행역인 구간 추가를 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("기존 하행 종점역이 상행역인 구간을 등록")
    @Test
    void addLineSectionAsLast() {
        // when
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 판교역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 판교역);
    }

    /**
     * When 역 사이에 기존 역 사이 길이보다 크거나 같은 구간 추가를 요청하면
     * Then 400에러가 발생하고
     * Then 에러 메시지를 응답받는다
     */
    @DisplayName("역 사이에 기존 역 사이 길이보다 크거나 같은 구간을 등록")
    @Test
    void addLineSectionWithInvalidDistance() {
        // when
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        // then
        assertThat(response.body().asString()).isEqualTo("역 사이에 기존 역 사이 길이보다 크거나 같은 구간을 등록할 수 없습니다");
    }

    /**
     * When 상행역과 하행역이 이미 모두 노선에 등록되어 있는 구간 추가를 요청하면
     * Then 에러 메시지를 응답받는다
     */
    @DisplayName("상행역과 하행역이 이미 모두 노선에 등록되어 있는 구간을 등록")
    @Test
    void addAlreadyExistentStationsAsNewLineSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        // then
        assertThat(response.body().asString()).isEqualTo("새로운 구간의 상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.");
    }

    /**
     * When 상행역과 하행역이 모두 노선에 등록되어 있지 않은 구간 추가를 요청하면
     * Then 에러 메시지를 응답받는다
     */
    @DisplayName("상행역과 하행역이 모두 노선에 등록되어 있지 않은 구간을 등록")
    @Test
    void addNonExistentStationsAsNewLineSection() {
        // when
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 6));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        // then
        assertThat(response.body().asString()).isEqualTo("새로운 구간의 상행역과 하행역이 모두 노선에 등록되어 있지 않습니다.");
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
