package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 신규역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        신규역 = 지하철역_생성_요청("신규역").jsonPath().getLong("id");

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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 기존 구간에 새로 등록된 역을 포함하는 새로운 구간을 추가하면
     * Then 기존 구간 사이에 새로운 구간이 추가된다.
     * AS-IS    A ----------------- C
     *        + A ------- B
     * TO-BE    A --------B-------- C
     */
    @DisplayName("기존 구간의 상행역을 기준으로 새로운 구간을 추가한다.")
    @Test
    void addIntermediateSectionExistingUpStation() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 신규역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 신규역, 양재역);
    }

    /**
     * When 기존 구간에 새로 등록된 역을 포함하는 새로운 구간을 추가하면
     * Then 기존 구간 사이에 새로운 구간이 추가된다.
     * AS-IS    A ----------------- C
     *        +           B ------- C
     * TO-BE    A --------B-------- C
     */
    @DisplayName("기존 구간의 하행역을 기준으로 새로운 구간을 추가한다.")
    @Test
    void addIntermediateSectionExistingDownStation() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신규역, 양재역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 신규역, 양재역);
    }

    /**
     * When 새로운 구간 등록 요청 시, 상행역과 하행역이 이미 모두 노선에 등록되어 있다면
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("새로운 구간 추가 시, 상행역과 하행역 모두 노선에 등록되어 있지 않아야 한다.")
    @Test
    void bothStationsExistInLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 지하철 노선에 새로운 역과 구간을 추가하고
     * When 새로운 구간을 추가하려고 할 때, 구간의 길이가 기존 구간의 길이보다 크거나 같으면
     * Then 새로운 구간이 추가되지 않는다.
     */
    @DisplayName("새로 추가하려는 구간의 길이는 기존 구간의 길이보다 작아야 한다.")
    @Test
    void invalidSectionDistance() {
        // given
        Long 에러역 = 지하철역_생성_요청("에러역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신규역, 양재역));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(에러역, 신규역));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * When 노선에 새로운 역이 상행 종점인 구간을 등록하면
     * Then 기존 구간 사이에 새로운 구간이 추가된다.
     * AS-IS              B-------- C
     *        + A---------B
     * TO-BE    A --------B-------- C
     */
    @DisplayName("새로운 역을 상행 종점으로 갖는 구간을 등록한다.")
    @Test
    void addSectionWithFirstUpStation() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신규역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신규역, 강남역, 양재역);
    }

    /**
     * When 노선에 새로운 역이 하행 종점인 구간을 등록하면
     * Then 기존 구간 사이에 새로운 구간이 추가된다.
     * AS-IS    A---------B
     *        +           B-------- C
     * TO-BE    A --------B-------- C
     */
    @DisplayName("새로운 역을 하행 종점으로 갖는 구간을 등록한다.")
    @Test
    void addSectionWithLastDownStation() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 신규역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 신규역);
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
}
