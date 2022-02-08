package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.util.Arrays;
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

        Map<String, Object> lineCreateParams = createLineCreateParams(강남역, 양재역, 10);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * Scenario : 한 구간에 대해 하행역에 새로운 구간이 추가되는 경우
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선의 최하행역에 대해 구간을 등록")
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
     * Scenario : 한 구간에 대해 상행역이 같고 하행역이 다른 경우 구간을 분할하여 등록한다.
     * given    : 새로운 역이 생성되고
     * when     : 기존 구간의 상행을 상행, 새로운 역을 하행역으로 갖는 구간을 등록하면
     * then     : 구간이 새롭게 등록된다.
     * (기존상행-새로운하행) - (새로운하행-기존하행)
     */
    @DisplayName("구간 사이에 구간을 새롭게 등록한다. - 상행역이 같은경우")
    @Test
    void addDividedSection() {
        // given
        long 역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");

        // when
        Map<String, Object> postRequest = createSectionCreateParams(강남역, 역삼역, 8);
        ExtractableResponse<Response> postResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, postRequest);

        // then
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.name"))
                .containsExactly(Arrays.array("강남역", "역삼역", "양재역"));
    }

    /**
     * Scenario : 한 구간에 대해 하행역이 같고 상행역이 다른 경우 구간을 분할하여 등록한다.
     * given    : 새로운 역이 생성되고
     * when     : 새로운 역을 상행역, 기존 구간의 하행역을 하행역으로 갖는 구간을 등록하면
     * then     : 구간이 새롭게 등록된다.
     * (기존상행-새로운하행) - (새로운하행-기존하행)
     */
    @DisplayName("구간 사이에 구간을 새롭게 등록한다. - 하행역이 같은경우")
    @Test
    void addDividedSection2() {
        // given
        long 역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");

        // when
        Map<String, Object> postRequest = createSectionCreateParams(역삼역, 양재역, 8);
        ExtractableResponse<Response> postResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, postRequest);

        // then
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.name"))
                .containsExactly(Arrays.array("강남역", "역삼역", "양재역"));
    }

    /**
     * Scenario : 한 구간에 대해 상행역에 새로운 구간이 추가되는 경우
     * given    : 새로운 역이 생성되고
     * when     : 새로운 역이 기존 구간의 상행역인 경우 구간을 등록하면
     * then     : 구간이 새롭게 등록된다.
     * (새로운상행-기존상행) - (기존상행-기존하행)
     */
    @DisplayName("구간의 상행역에 새로운 상행역이 추가되는 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        long 역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");

        // when
        Map<String, Object> postRequest = createSectionCreateParams(역삼역, 강남역, 8);
        ExtractableResponse<Response> postResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, postRequest);

        // then
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.name"))
                .containsExactly(Arrays.array("역삼역", "강남역", "양재역"));
    }

    /**
     * Scenario : 종점에 대해 구간삭제를 요청하면 구간이 지워지고, 노선의 종점이 직전의 상행역으로 갱신된다.
     * given    : 새로운 역을 추가하고 기존 구간의 하행에 대해 구간을 생성한다.
     * when     : 추가한 역에 대해 구간 삭제를 요청하면
     * then     : 구간이 삭제되고, 노선의 종점이 기존 구간의 하행으로 갱신된다.
     */
    @DisplayName("종점에 대해 구간삭제를 요청하면, 구간이 삭제되고 종점이 갱신된다.")
    @Test
    void removeLineSection() {
        // given
        long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(신분당선);
        assertThat(getResponse.body().jsonPath().getString("downStation")).isEqualTo("양재역");
        assertThat(getResponse.body().jsonPath().getList("stations.name"))
                .containsExactly(Arrays.array("강남역", "양재역"));
    }

    /**
     * Scenario : 최상행역에 대해 구간삭제를 요청하면 구간이 지워지고, 노선의 최상행역이 직후의 하행역으로 갱신된다.
     * given    : 새로운 역을 추가하고 기존 구간의 하행에 대해 구간을 생성한다.
     * when     : 기존 구간 상행역에 대해 구간 삭제를 요청하면
     * then     : 구간이 삭제되고, 노선의 최상행역이 기존 구간의 하행역으로 갱신된다.
     */
    @DisplayName("최상행역에 대해 구간삭제를 요청하면, 구간이 삭제되고 최상행역이 갱신된다.")
    @Test
    void removeLineSection2() {
        // given
        long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(신분당선);
        assertThat(getResponse.body().jsonPath().getString("upStation")).isEqualTo("양재역");
        assertThat(getResponse.body().jsonPath().getList("stations.name"))
                .containsExactly(Arrays.array("양재역", "정자역"));
    }

    /**
     * Scenario : 중간역에 대해 삭제를 요청하면 구간이 삭제되고 상행과 하행을 잇는 구간이 새롭게 추가된다.
     * given    : 새로운 역을 추가하고 기존 구간의 하행에 대해 구간을 생성한다.
     * when     : 중간역에대해 삭제를 요청하면
     * then     : 구간이 삭제되고, 새롭게 잇는 구간이 추가된다.
     */
    @DisplayName("중간역에 대해 삭제를 요청하면, 구간이 삭제되고 상행과 하행을 잇는 구간이 추가된다.")
    @Test
    void removeLineSection3() {
        // given
        long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(신분당선);
        assertThat(getResponse.body().jsonPath().getList("stations.name"))
                .containsExactly(Arrays.array("강남역", "정자역"));
    }

    /**
     * Scenario : 구간이 1개인 노선에 대해 구간 삭제를 요청하면 삭제가 되지 않는다.
     * when     : 주어진 노선의 하행역에 대해 구간삭제를 요청하면
     * then     : 구간이 삭제되어지지 않는다. (400에러)
     */
    @DisplayName("구간이 1개인 노선은 구간삭제를 하지 못한다.")
    @Test
    void validateRemoveLineSection() {
        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Scenario : 구간에 존재하지 않은 역에 대해 삭제 요청을 하면 삭제가 되지 않는다.
     * given    : 새로운 역을 추가하고 구간을 등록한 후에
     * when     : 주어진 노선에 존재하지 않은 역에대해 삭제를 요청하면
     * then     : 구간이 삭제되어지지 않는다. (404에러)
     */
    @DisplayName("노선에 존재하지 않은 역에대해 구간삭제가 불가하다.")
    @Test
    void validateRemoveLineSection2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 8));
        Long 용산역 = 지하철역_생성_요청("용산역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 용산역);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Scenario: 역 사이에 새로운 역 등록시 기존 구간의 길이보다 길이가 같거나 크면 에러를 반환한다.
     * given   : 새로운 역을 생성하고
     * when    : 기존 구간의 길이보다 큰 거리의 구간을 기존 구간 사이에 추가 요청을 하면
     * then    : 구간 추가가 되지 않는다. (400에러)
     */
    @DisplayName("역 사이에 새로운 역 등록시 기존 구간의 길이보다 크거나 같으면 안된다.")
    @Test
    void validateAddSection() {
        // given
        long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> postResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 11));

        // then
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Scenario: 추가될 구간의 두 역이 모두 기존 노선에 포함되어 있다면 에러를 반환한다.
     * when    : 기존 노선의 두 역으로 새로운 구간 생성을 요청하면
     * then    : 구간 추가가 되지 않는다. (409 에러)
     */
    @DisplayName("추가될 구간의 두 역이 노선에 이미 모두 등록된 상태면 안된다.")
    @Test
    void validateAddSection2() {
        // when
        ExtractableResponse<Response> postResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 강남역, 10));

        // then
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    /**
     * Scenario: 추가될 구간의 두 역이 모두 기존 노선에 존재하지 않다면 에러를 반환한다.
     * given   : 새로운 역을 2개 생성하고
     * when    : 두 역으로 새로운 구간 생성을 요청하면
     * then    : 구간 추가가 되지 않는다. (404 에러)
     */
    @DisplayName("추가될 구간의 두 역이 노선에 모두 없어서는 안된다.")
    @Test
    void validateAddSection3() {
        // given
        Long 역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        Long 선릉역 = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> postResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(역삼역, 선릉역, 10));

        // then
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private Map<String, Object> createLineCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, Object> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId);
        lineCreateParams.put("downStationId", downStationId);
        lineCreateParams.put("distance", distance);
        return lineCreateParams;
    }

    private Map<String, Object> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }
}
