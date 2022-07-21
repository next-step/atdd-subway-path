package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.AssertUtils;
import nextstep.subway.utils.ResponseUtils;
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
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = ResponseUtils.getLong(지하철역_생성_요청("강남역"), "id");
        양재역 = ResponseUtils.getLong(지하철역_생성_요청("양재역"), "id");

        Map<String, String> lineCreateParams = createLineParams(강남역, 양재역, 10);
        신분당선 = ResponseUtils.getLong(지하철_노선_생성_요청(lineCreateParams), "id");
    }

    /**
     * Given 새로운 지하철 추가를 요청하고
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // given
        Long 정자역 = ResponseUtils.getLong(지하철역_생성_요청("정자역"), "id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 7));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        AssertUtils.lineSection(response, 강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청하고
     * When 추가 요청한 구간 중간에 새로운 구간을 추가하면
     * Then 기존 구간 사이에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 구간 중간에 새로운 역 추가")
    @Test
    void addBetweenSection() {
        // given
        Long 정자역 = ResponseUtils.getLong(지하철역_생성_요청("정자역"), "id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 7));

        // when
        Long 양재시민의숲역 = ResponseUtils.getLong(지하철역_생성_요청("양재시민의숲역"), "id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 양재시민의숲역, 4));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        AssertUtils.lineSection(response, 강남역, 양재역, 양재시민의숲역, 정자역);
    }

    /**
     * When 지하철 노선에 새로운 상행 종점을 추가를 요청 하면
     * Then 새로운 상행 종점이 구간에 추가된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void addNewUpStationAtSection() {
        // when
        Long 신논현역 = ResponseUtils.getLong(지하철역_생성_요청("신논현역"), "id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(신논현역, 강남역, 2));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        AssertUtils.lineSection(response, 신논현역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선에 기존 구간과 거리가 같은 새로운 구간을 등록하면
     * Then 예외가 발생한다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void given_newSection_when_sameDistance_then_exception() {
        // given
        Long 신논현역 = ResponseUtils.getLong(지하철역_생성_요청("신논현역"), "id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(강남역, 신논현역, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 상행역과 하행역이 이미 등록된 구간을 추가하면
     * Then 예외가 발생한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void given_newSection_when_duplicatedStations_then_exception() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(강남역, 양재역, 5));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 상행역과 하행역이 포함되어 있지 않은 구간을 추가하면
     * Then 예외가 발생한다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void given_newSection_when_unknownInSection_then_exception() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(강남역, 양재역, 5));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 하행 종점역 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선의 하행 종점역 제거")
    @Test
    void removeLineSectionAtLastStation() {
        // given
        Long 정자역 = ResponseUtils.getLong(지하철역_생성_요청("정자역"), "id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 7));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        AssertUtils.lineSection(response, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 상행 종점역 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선의 상행 종점역 삭제")
    @Test
    void removeStationInSectionAtFirstStation() {
        // given
        Long 정자역 = ResponseUtils.getLong(지하철역_생성_요청("정자역"), "id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 7));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        AssertUtils.lineSection(response, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간 지하철역 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 구간의 중간 지하철역 제거")
    @Test
    void removeStationInSection() {
        // given
        Long 정자역 = ResponseUtils.getLong(지하철역_생성_요청("정자역"), "id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(양재역, 정자역, 7));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        AssertUtils.lineSection(response, 강남역, 정자역);
    }

    /**
     * When 지하철 노선에 구간이 하나인 경우, 하행 종점역 제거를 요청 하면
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 구간이 하나인 하행 종점역을 제거하면 예외가 발행한다.")
    @Test
    void removeLastStationInOneSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 구간이 하나인 경우, 하행 종점역 제거를 요청 하면
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 구간이 하나인 하행 종점역을 제거하면 예외가 발행한다.")
    @Test
    void removeFirstStationInOneSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 새로운 지하철역을 등록하고
     * When 등록한 지하철역을 지하철 노선에서 삭제 요청 하면
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 구간이 하나인 하행 종점역을 제거하면 예외가 발행한다.")
    @Test
    void removeUnknownStationInOneSection() {
        // given
        Long 정자역 = ResponseUtils.getLong(지하철역_생성_요청("정자역"), "id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createLineParams(Long upStationId, Long downStationId, int distance) {
        return Map.of(
                "name", "신분당선",
                "color", "bg-red-600",
                "upStationId", upStationId.toString(),
                "downStationId", downStationId.toString(),
                "distance", String.valueOf(distance)
        );
    }

    private Map<String, String> createSectionParams(Long upStationId, Long downStationId, int distance) {
        return Map.of(
                "upStationId", upStationId.toString(),
                "downStationId", downStationId.toString(),
                "distance", String.valueOf(distance)
        );
    }
}
