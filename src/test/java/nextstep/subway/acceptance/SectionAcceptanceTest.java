package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.AssertResponse.응답_성공_검증;
import static nextstep.subway.acceptance.AssertResponse.응답_실패_검증;
import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.fixture.LineFixture.createLineCreateParams;
import static nextstep.subway.fixture.SectionFixture.createSectionCreateParams;
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

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, 10);
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
        응답_성공_검증(response);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선에 상행 좀점 추가 요청을 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 새로운 상행 종점을 등록")
    @Test
    void addLineSection2() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        응답_성공_검증(response);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선 구간 사이에 새로운 역을 추가 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 중간에 새로운 역을 등록")
    @Test
    void addLineSection3() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        응답_성공_검증(response);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 지하철 노선에 기존 구간보다 거리가 더 긴 구간을 추가 하면
     * Then 구간 추가가 실패한다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addLineSection3_2() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 11));

        // then
        응답_실패_검증(response);
    }

    /**
     * When 지하철 노선에 하행 종점 추가 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 새로운 하행 종점을 등록")
    @Test
    void addLineSection4() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        응답_성공_검증(response);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선에 이미 상행역과 하행역이 노선에 등록된 구간을 추가 요청하면
     * Then 추가에 실패한다
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addLineSection4_2() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 강남역));

        // then
        응답_실패_검증(response);
    }

    /**
     * When 지하철 노선에 상행역과 하행역 둘 중 하나도 포함되어있지 않은 구간을 추가 요청 하면
     * Then 추가에 실패한다
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addLineSection5() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역));

        // then
        응답_실패_검증(response);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 처음 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 처음 구간을 제거")
    @Test
    void removeLineSection1() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        응답_성공_검증(response);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 중간 구간을 제거")
    @Test
    void removeLineSection2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 1));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        응답_성공_검증(response);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 마지막 구간을 제거")
    @Test
    void removeLineSection3() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        응답_성공_검증(response);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 지하철 노선의 구간 제거를 요청 하면
     * Then 노선에 구간이 제거에 실패한다
     */
    @DisplayName("지하철 노선에 구간이 하나일때 제거")
    @Test
    void removeLineSection4() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        응답_실패_검증(response);
    }

    /**
     * When 지하철 노선에 없는 역으로 구간 제거를 요청 하면
     * Then 노선에 구간이 제거에 실패한다
     */
    @DisplayName("지하철 노선에 없는 역을 제거")
    @Test
    void removeLineSection5() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 100L);

        // then
        응답_실패_검증(response);
    }
}
