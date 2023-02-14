package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[Acceptance] 지하철 구간 관리 기능")
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
     * Given 역과 구간을 추가로 등록하고
     * When 지하철 노선 구간 사이에 새로운 구간을 추가하면
     * Then 노선에 새로운 구간이 기존 구간들 사이에 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // given
        Long 역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 역삼역, 5));

        // then
        var response = 지하철_노선_조회_요청(신분당선);
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 역삼역, 정자역);
        });
    }

    /**
     * When 지하철 노선에 새로운 구간을 상행 종점으로 추가하면
     * Then 노선에 새로운 구간이 상행 종점으로 추가된다.
     */
    @DisplayName("지하철 노선에 상행 종점 구간을 등록")
    @Test
    void addUpEndSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 10));

        // then
        var response = 지하철_노선_조회_요청(신분당선);
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
        });
    }

    /**
     * When 지하철 노선에 새로운 구간을 하행 종점으로 추가하면
     * Then 노선에 마지막 구간에 하행 종점으로 추가된다.
     */
    @DisplayName("지하철 노선에 하행 종점 구간을 등록")
    @Test
    void addDownEndSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        // then
        var response = 지하철_노선_조회_요청(신분당선);
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
        });
    }

    /**
     * When 지하철 노선에 기존 역 사이 길이보다 큰 구간을 추가하면
     * Then 구간을 등록할 수 없다.
     */
    @DisplayName("기존 역 사이 길이보다 큰 구간을 추가할 수 없다")
    @Test
    void addLongSection() {
        // when
        var 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        var 강남역_정자역_구간_요청 = createSectionCreateParams(강남역, 정자역, 100);
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 강남역_정자역_구간_요청);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 이미 등록 되어있는 구간을 추가하면
     * Then 구간을 등록할 수 없다.
     */
    @DisplayName("이미 등록된 구간을 추가할 수 없다")
    @Test
    void addExistingSection() {
        // when
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 5));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 상행역과 하행역 둘 중 하나도 포함되어있지 않은 구간을 추가하면
     * Then 구간을 등록할 수 없다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나라도 포함되어 있지 않은 구간은 추가할 수 없다.")
    @Test
    void addInvalidSection() {
        // when
        var 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        var 역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 역삼역, 15));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 여러개의 구간을 양방향으로 추가하고
     * When 지하철 노선을 조회하면
     * Then 상행역 부터 하행역 방향으로 정렬된 역을 확인할 수 있다.
     */
    @DisplayName("구간을 순서없이 등록해도 상행역부터 순서대로 조회된다.")
    @Test
    void showStations() {
        // given
        var 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        var 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        var 양재역_정자역_구간_추가_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));
        var 신논현역_강남역_구간_추가_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 10));

        // when
        var response = 지하철_노선_조회_요청(신분당선);

        // then
        assertAll(() -> {
            assertThat(양재역_정자역_구간_추가_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(신논현역_강남역_구간_추가_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신논현역, 강남역, 양재역, 정자역);
        });
    }


    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 마지막 구간을 제거")
    @Test
    void removeLineEndSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        var response = 지하철_노선_조회_요청(신분당선);
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
        });
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 중간에 있는 역을 제거하면
     * Then 해당 구간이 제거된다
     */
    @DisplayName("지하철 노선에 중간 구간 제거")
    @Test
    void removeLineBetweenSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        var response = 지하철_노선_조회_요청(신분당선);
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
        });
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 중간에 있는 역을 모두 제거하면
     * Then 해당 구간이 제거된다
     */
    @DisplayName("지하철 노선에 중간 구간을 모두 제거")
    @Test
    void removeAllLineBetweenSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        var response = 지하철_노선_조회_요청(신분당선);
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 미금역);
        });
    }

    /**
     * Given 구간을 삭제할 수 있게 구간을 추가로 등록하고
     * When 등록되어있지 않은 역을 제거하면
     * Then 제거할 수 없다
     */
    @DisplayName("등록되지 않은 역을 제거할 수 없다")
    @Test
    void removeLineSectionWithUnregisteredStation() {
        // given
        Long 존재하지_않는_역 = Long.MAX_VALUE;
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        var deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 존재하지_않는_역);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * When 구간이 1개 이하일 때 역을 제거하면
     * Then 제거할 수 없다
     */
    @DisplayName("구간이 1개인 경우 제거 불가")
    @Test
    void removeLineSingleSection() {
        // when
        var deleteResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
