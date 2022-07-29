package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        assertStatusCodeAndStationIds(강남역, 양재역, 정자역);
    }

    /**
     * When 새로운 역을 상행 종점으로 등록하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 새로운 역을 상행 종점으로 추가")
    @Test
    void addUpStationSection() {
        // when
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역));

        // then
        assertStatusCodeAndStationIds(신논현역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선에 역 사이에 상행역이 같은새로운 역을 등록하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 구간에 상행역이 같은 구간을 추가")
    @Test
    void addStationSameUpStationInSection() {

        // when
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, 3));

        // then
        assertStatusCodeAndStationIds(강남역, 중간역, 양재역);
    }

    /**
     * When 지하철 노선에 역 사이에 하행역이 같은새로운 역을 등록하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 구간에 하행역이 같은 구간을 추가")
    @Test
    void addStationSameDownStationInSection() {

        // when
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(중간역, 양재역, 3));

        // then
        assertStatusCodeAndStationIds(강남역, 중간역, 양재역);
    }


    /**
     * When 지하철 노선에 역 사이에 새로운 역 등록 시
     * 새로운 구간의 역 길이가 같거나 크면
     *
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선에 역 사이에 새로운 역 등록 시 새로운 구간의 역 길이가 같거나 크면 에러 발생")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void addStationInSectionDistanceException(int distance) {

        // when
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(중간역, 양재역, distance));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("추가 될 구간의 거리가 크거나 같을 수 없습니다.");
    }


    /**
     * When 지하철 노선에 구간 추가 시 이미 존재하는 구간이 있다면
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선에 구간 추가 시 이미 상행역, 하행역이 모두 있다면 에러 발생")
    @Test
    void addSectionUpStationDownStationAlreadyExist() {

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("이미 구간 내 상행역, 하행역이 모두 존재하여 추가할 수 없습니다.");
    }


    /**
     * When 지하철 노선에 구간 추가 시
     * 상행역과 하행역이 둘 중 하나도 포함되어 있지 않다면
     * <p>
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선에 구간 추가 시 상행역과 하행역 둘 중 하나도 포함되지 않았다면 에러가 발생")
    @Test
    void addSectionUpStationDownStationNotExist() {

        // when
        Long 새로운역1 = 지하철역_생성_요청("새로운역1").jsonPath().getLong("id");
        Long 새로운역2 = 지하철역_생성_요청("새로운역2").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(새로운역1, 새로운역2, 5));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("추가하고자 하는 상행역, 하행역이 존재하지 않아 추가할 수 없습니다.");
    }


    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 마지막 구간을 제거")
    @Test
    void removeLineLastSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        assertStatusCodeAndStationIds(강남역, 양재역);
    }


    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 첫 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 첫 구간을 제거")
    @Test
    void removeLineFirstSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertStatusCodeAndStationIds(양재역, 정자역);
    }


    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 가운데 역 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 가운데 구간을 제거")
    @Test
    void removeLineBetweenSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertStatusCodeAndStationIds(강남역, 정자역);
    }


    /**
     * When 지하철 노선의 가운데 역 제거 시 구간이 하나밖에 없으면
     * Then 에러가 발생한다
     */
    @DisplayName("역 삭제 시 구간이 하나 밖에 없으면 에러가 발생")
    @Test
    void removeOnlyOneSectionException() {

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("상행역과 하행역만 존재하기 때문에 삭제할 수 없습니다.");
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 존재하지 않는 역 삭제 요청 시
     * Then 에러가 발생한다
     */
    @DisplayName("존재하지 않는 역에 대한 삭제 시 에러가 발생")
    @Test
    void removeNotExistStationException() {

        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        Long 성내역 = 지하철역_생성_요청("성내역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 성내역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("구간이 존재하지 않아 역을 삭제할 수 없습니다.");
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private void assertStatusCodeAndStationIds(Long... 역들) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(역들);
    }

}
