package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 판교역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams("신분당선", "bg-red-600", 강남역, 판교역, 10);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 새로운 역을 상행 종점으로 지하철 노선 구간 등록 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("새로운 역을 상행 종점으로 지하철 노선 구간을 등록")
    @Test
    void addLineSectionAtFirst() {
        // when
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신논현역, 강남역, 판교역);
    }

    /**
     * When 새로운 역을 역 사이에 지하철 노선 구간 등록 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("새로운 역을 역 사이에 지하철 노선 구간을 등록")
    @Test
    void addLineSectionBetweenStation() {
        // when
        Long 양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 판교역);
    }

    /**
     * When 새로운 역을 하행 종점으로 지하철 노선에 구간을 등록 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("새로운 역을 하행 종점으로 지하철 노선에 구간을 등록")
    @Test
    void addLineSectionAtLast() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 판교역, 정자역);
    }

    /**
     * When 기존 역 사이 길이와 동일한 새로운 역을 역 사이에 지하철 노선 구간 등록 요청 하면
     * Then 노선에 새로운 구간이 추가되지 않는다
     */
    @DisplayName("기존 역 사이 길이와 동일한 새로운 역을 역 사이에 지하철 노선 구간을 등록")
    @Test
    void addLineSectionBetweenStationAndSameDistance() {
        // when
        Long 양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 기존 역을 지하철 노선 구간 등록 요청 하면
     * Then 노선에 새로운 구간이 추가되지 않는다
     */
    @DisplayName("기존 역을 지하철 노선 구간을 등록")
    @Test
    void addLineSectionBetweenStationAndSameStations() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 판교역, 6));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 기존 역과 연결되지 않는 지하철 노선 구간 등록 요청 하면
     * Then 노선에 새로운 구간이 추가되지 않는다
     */
    @DisplayName("기존 역과 연결되지 않는 새로운 역을 지하철 노선 구간을 등록")
    @Test
    void addLineSectionByNoMatchStations() {
        // when
        Long 양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        Long 양재시민의숲 = 지하철역_생성_요청("양재시민의숲").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재시민의숲, 6));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선 구간의 마지막 역 제거를 요청 하면
     * Then 해당하는 역이 구간에서 제거된다
     */
    @DisplayName("지하철 노선 구간의 마지막 역 제거")
    @Test
    void removeLineSectionOfLast() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 판교역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선 구간의 가운데 역 제거를 요청 하면
     * Then 해당하는 역이 구간에서 제거된다
     */
    @DisplayName("지하철 노선 구간의 중간 역을 제거")
    @Test
    void removeLineSectionOfMiddle() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 판교역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선 구간의 첫번째 역 제거를 요청 하면
     * Then 해당하는 역이 구간에서 제거된다
     */
    @DisplayName("지하철 노선 구간의 첫번째 역을 제거")
    @Test
    void removeLineSectionOfFirst() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(판교역, 정자역);
    }

    /**
     * When 지하철 노선 마지막 구간의 역 제거를 요청 하면
     * Then 에러 발생한다
     */
    @DisplayName("지하철 노선 마지막 구간의 역을 제거")
    @Test
    void removeLineSectionOfLastSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선 구간에 존재하지 않는 역 제거를 요청 하면
     * Then 에러 발생한다
     */
    @DisplayName("지하철 노선 구간에 존재하지 않는 역을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 흑석역 = 지하철역_생성_요청("흑석역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 흑석역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
