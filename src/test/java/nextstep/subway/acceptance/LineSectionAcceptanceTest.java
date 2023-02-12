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
     * When 지하철 노선 하행 종점에 새로운 구간 추가를 요청 하면 (A, B) + (B, C)
     * Then 노선에 새로운 구간이 추가된다. (A, B, C)
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선 중간에 새로운 구간 추가를 요청 하면 (A, B) + (A, C)
     * Then 노선에 새로운 구간이 추가된다. (A, C, B)
     */
    @Test
    @DisplayName("지하철 노선 중간에 새로운 구간 추가 요청 (A, B) + (A, C)")
    void test1() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 5L));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }


    /**
     * When 지하철 노선 중간에 새로운 구간 추가를 요청 하면 (A, B) + (C, B)
     * Then 노선에 새로운 구간이 추가된다. (A, C, B)
     */
    @Test
    @DisplayName("지하철 노선 중간에 새로운 구간 추가 요청 (A, B) + (A, C)")
    void test2() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 5L));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 지하철 노선 앞에 새로운 구간 추가를 요청 하면 (A, B), (C, A)
     * Then 노선에 새로운 구간이 추가된다. (C, A, B)
     */
    @Test
    @DisplayName("지하철 노선 중간에 새로운 구간 추가 요청 (A, B) + (C, A)")
    void test3() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 5L));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * When 기존 지하철 구간 사이에 더 긴 구간의 추가를 요청하면 (A, B : 10m) + (A, C : 20m)
     * Then 노선 구간 추가 실패 오류가 나온다. ("기존 지하철 구간의 길이보다 추가된 구간의 길이가 더 깁니다.")
     */
    @Test
    @DisplayName("지하철 노선 중간에 새로운 구간 추가 요청 (A, B : 10m) + (A, C : 20m)")
    void test4() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 20L));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).contains("기존 지하철 구간의 길이보다 추가된 구간의 길이가 더 깁니다.");
    }

    /**
     * When 기존 지하철 구간 사이에 더 긴 구간의 추가를 요청하면 (A, B : 10m) + (C, B : 20m)
     * Then 노선 구간 추가 실패 오류가 나온다. ("기존 지하철 구간의 길이보다 추가된 구간의 길이가 더 깁니다.")
     */
    @Test
    @DisplayName("지하철 노선 중간에 새로운 구간 추가 요청 (A, B : 10m) + (C, B : 20m)")
    void test5() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 20L));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).contains("기존 지하철 구간의 길이보다 추가된 구간의 길이가 더 깁니다.");
    }

    /**
     * When 기존 지하철 노선에 포함되어 있는 지하철 역들 사이에서 새로운 구간 추가를 요청하면 (A, B) + (B, A)
     * Then 노선 구간 추가 실패 오류가 나온다. ("노선에 존재하던 지하철 역 끼리는 구간을 만들 수 없습니다.")
     */
    @Test
    @DisplayName("기존 지하철 노선에 포함되어 있는 지하철 역들 사이에서 새로운 구간 추가를 요청 (A, B) + (B, A)")
    void test6() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 강남역, 20L));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).contains("노선에 존재하던 지하철 역 끼리는 구간을 만들 수 없습니다.");
    }

    /**
     * When 새로운 구간의 지하철 역들이 기존 지하철 노선에 하나도 포함되어 있지 않다면 (A, B) + (C, D)
     * Then 노선 구간 추가 실패 오류가 나온다. ("새로운 구간의 지하철 역들 중 하나는 기존 지하철 노선에 포함되어 있어야 합니다.")
     */
    @Test
    @DisplayName("새로운 구간의 지하철 역들이 기존 지하철 노선에 하나도 포함되어 있지 않다. (A, B) + (C, D)")
    void test7() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 신사역 = 지하철역_생성_요청("신사역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 신사역, 20L));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).contains("새로운 구간의 지하철 역들 중 하나는 기존 지하철 노선에 포함되어 있어야 합니다.");
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", 6 + "");
        return params;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
