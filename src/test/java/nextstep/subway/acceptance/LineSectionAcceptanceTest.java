package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        Map<String, String> lineCreateParams = createLineParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간(기존 구간의 상행 종점역이 일치하면서 하행 종점역은 존재하지 않는 역)을 추가 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 사이에 새로운 구간을 등록 - 구간의 상행 종점역 일치")
    @Test
    void addLineSectionOfBetweenLines1() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 10));

        // when
        var 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 판교역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 판교역, 정자역);
    }

    /**
     * When 지하철 노선에 새로운 구간(기존 구간의 하행 종점역이 일치하면서 상행 종점역은 존재하지 않는 역)을 추가 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 사이에 새로운 구간을 등록 - 구간의 하행 종점역 일치")
    @Test
    void addLineSectionOfBetweenLines2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 10));

        // when
        var 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(판교역, 정자역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 판교역, 정자역);
    }

    /**
     * When 지하철 노선에 새로운 구간(기존 구간의 하행 종점역이 일치, 상행 종점역은 존재하지 않는 역, 기존 구간과 길이가 같음)을 추가 요청 하면
     * Then 노선에 새로운 구간이 추가에 실패한다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addLineSectionFailCase1() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 10));

        // when
        var 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(판교역, 정자역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(500);
    }

    /**
     * When 지하철 노선에 기존 구간을 추가 요청 하면
     * Then 노선에 새로운 구간이 추가에 실패한다
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    void addLineSectionFailCase2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 10));

        // when
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(500);
    }

    /**
     * When 지하철 노선에 상행역과 하행역 둘중 하나도 포함되어 있지 않는 구간을 추가 요청 하면
     * Then 노선에 새로운 구간이 추가에 실패한다
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지않으면 추가할 수 없다")
    @Test
    void addLineSectionFailCase3() {
        // given
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(판교역, 정자역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(500);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 3));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 신분당선 노선에 상행역 강남역 하행역 양재역 구간이 등록되어 있다
     * When 노선의 상행 종점에 새로운 역(논현역)을 하행 종점(강남역)은 노선에 존재하는 새로운 구간을 추가 요청하면
     * Then 노선에 새로운 구간이 추가된다 (논현역-강남역 구간)
     * Then 해당 노선의 상행 종점역이 새로 추가한 구간의 상행 종점역이 된다 (신분당선의 상행 종점역 -> 논현역)
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우 노선에 새로운 구간이 추가된다")
    @Test
    void saveSectionOfNewUpStation() {
        // when
        var 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(논현역, 강남역, 3));

        // then
        var response = 지하철_노선_조회_요청(신분당선);
        final List<String> stationNames = response.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(stationNames).containsSequence("논현역", "강남역", "양재역");
    }

    /**
     * Given 신분당선 노선에 상행역 강남역 하행역 양재역 구간이 등록되어 있다
     * When 상행 종점역이 기존 노선의 하행 종점역(양재역)이고 하행 종점역이 새로운 역(판교역)인 구간을 추가 요청하면
     * Then 노선에 새로운 구간이 추가된다 (양재역-판교역 구간)
     * Then 해당 노선의 하행 종점역이 새로 추가한 구간의 하행 종점역이 된다 (신분당선의 하행 종점역 -> 판교역)
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우 노선에 새로운 구간이 추가된다")
    @Test
    void saveSectionOfNewDownStation() {
        // when
        var 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 판교역, 7));

        // then
        var response = 지하철_노선_조회_요청(신분당선);
        final List<String> stationNames = response.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(stationNames).containsSequence("강남역", "양재역", "판교역");
    }

    private static class StationResponse {
        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private Map<String, String> createLineParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

}
