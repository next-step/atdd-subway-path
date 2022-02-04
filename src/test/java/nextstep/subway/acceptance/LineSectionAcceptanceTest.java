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

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, 10);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * Given 새로운 지하철역 생성을 요청하고,
     * When 기존 상행 종점역에 새로운 구간 추가를 요청하면,
     * Then 상행 종점역에 새로운 구간이 추가되고, 조회 시 상행 종점부터 하행 종점까지 순서대로 조회된다.
     */
    @DisplayName("지하철 노선의 상행 종점역으로 새로운 구간 추가")
    @Test
    void addLineUpEndStationSection() {
        // given
        Long 신논현역 = 지하철역_생성_요청("신논현").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 10));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신논현역, 강남역, 양재역);
    }

    /**
     * Given 새로운 지하철역 생얼을 요청하고,
     * AND 새로운 구간 생성 요청을 하고,
     * Given 새로운 지하철역 생얼을 요청하고,
     * When 역 사이에 새로운 구간 추가를 요청하면,
     * Then 사이에 새로운 구간이 추가되고, 조회 시 상행 종점부터 하행 종점까지 순서대로 조회된다.
     */
    @DisplayName("지하철역 사이에 새로운 구간 추가")
    @Test
    void addLineBetweenSection() {
        // given
        Long 논현역 = 지하철역_생성_요청("논현").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 강남역, 10));
        Long 신논현역 = 지하철역_생성_요청("신논현").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 신논현역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(논현역, 신논현역, 강남역, 양재역);
    }

    /**
     * Given 새로운 지하철역 생성을 요청하고,
     * When 지하철 노선의 하행 종점역에 새로운 구간 추가를 요청하면,
     * Then 노선에 새로운 구간이 추가된다.
     */
    @DisplayName("지하철 노선의 하행 종점역에 구간을 추가")
    @Test
    void addLineDownEndStationSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철역 생성 요청을 하고,
     * AND 지하철 노선에 새로운 구간 추가를 요청하고,
     * When 지하철 노선의 상행 종점역 구간 제거를 요청하면,
     * Then 노선에 구간이 제거된다.
     */
    @DisplayName("지하철 노선에 상행 종점역 구간을 제거")
    @Test
    void removeLineUpEndSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    /**
     * Given 지하철역 생성 요청을 하고,
     * AND 지하철 노선에 새로운 구간 추가를 요청하고,
     * When 지하철 노선의 중간 역 구간 제거를 요청하면,
     * Then 노선에 구간이 제거된다.
     */
    @DisplayName("지하철 노선에 중간역 구간을 제거")
    @Test
    void removeLineBetweenSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    /**
     * Given 지하철역 생성 요청을 하고,
     * AND 지하철 노선에 새로운 구간 추가를 요청하고,
     * When 지하철 노선의 마지막 구간 제거를 요청하면,
     * Then 노선에 구간이 제거된다.
     */
    @DisplayName("지하철 노선에 하행 종점역 구간을 제거")
    @Test
    void removeLineDownEndSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 새로운 지하철역 생성을 요청하고,
     * AND 새로운 구간 생성을 요청을 하고,
     * Given 새로운 지하철역 생성을 요청하고,
     * When 역 사이에 새로운 구간 추가 시 기존 역 사이 거리 이상으로 구간 추가 요청을 하면,
     * Then 구간 추가가 실패한다.
     */
    @DisplayName("지하철역 사이에 새로운 구간 추가 시 기존 역 사이 길이 이상일 수 없음.")
    @Test
    void exceptionAddLineBetweenSection() {
        // given
        Long 논현역 = 지하철역_생성_요청("논현").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 강남역, 10));
        Long 신논현역 = 지하철역_생성_요청("신논현").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 신논현역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상행역과 하행역이 모두 존재하는 역을 구간추가 요청이면,
     * Then 구간 추가가 실패한다.
     */
    @DisplayName("구간 추가 시 상행역과 하행역이 모두 등록된 역일 수 없음.")
    @Test
    void exceptionAddSectionDuplicate() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 등록되지 않는 상행역을 생성하고,
     * AND 등록되지 않는 하행역을 생성하고,
     * When 구간 추가 요청하면,
     * Then 구간 추가가 실패한다.
     */
    @DisplayName("구간 추가 시 상행역과 하행역 중 하나도 구간에 등록되어 있지 않으면 등록 불가.(최초 등록 시 제외)")
    @Test
    void exceptionAddSectionNotFoundStation() {
        // given
        Long 논현역 = 지하철역_생성_요청("논현").jsonPath().getLong("id");
        Long 신논현역 = 지하철역_생성_요청("신논현").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 신논현역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 구간이 1개인 경우 삭제 요청을 하면,
     * Then 구간 제거가 실패한다.
     */
    @DisplayName("구간이 1개이면 삭제 불가.")
    @Test
    void exceptionOneSectionRemove() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 존해하지 않는 역을 삭제 요청하면,
     * Then 구간 제거가 실패한다.
     */
    @DisplayName("존재하지 않는 하행역 삭제 불가")
    @Test
    void exceptionNotSectionRemove() {
        // given
        Long 논현역 = 지하철역_생성_요청("논현").jsonPath().getLong("id");
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 논현역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", String.valueOf(upStationId));
        lineCreateParams.put("downStationId", String.valueOf(downStationId));
        lineCreateParams.put("distance", String.valueOf(distance));
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }
}
