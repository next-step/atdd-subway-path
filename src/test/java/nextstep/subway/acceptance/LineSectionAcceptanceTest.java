package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    private int givenDistance;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @ParameterizedTest(name = "[{index}] distance = {0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    @interface ValidDistanceTest {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @ParameterizedTest(name = "[{index}] distance = {0}")
    @ValueSource(ints = {10, 11, 12, 13, 14})
    @interface TooMuchDistanceTest {

    }

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        givenDistance = 10;

        신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역, 양재역, givenDistance)
            .jsonPath().getLong("id");
    }

    /**
     * Given 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작고,
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @ValidDistanceTest
    void addLineSection(int distance) {
        // given
        지하찰_노선에_등록될_구간의_거리가_기존_역_사이_길이_보다_작다(distance);

        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, 정자역, distance);

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
        지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, 정자역, 6);

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작고,
     * When 새로운 역을 상행 종점으로 등록할 경우,
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우, 지하철 노선에 구간을 등록")
    @ValidDistanceTest
    void addLineSectionInBaseUpStation(int distance) {
        // given
        지하찰_노선에_등록될_구간의_거리가_기존_역_사이_길이_보다_작다(distance);

        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 정자역, 강남역, distance);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * Given 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작고,
     * When 새로운 역을 하행 종점으로 등록할 경우,
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우, 지하철 노선에 구간을 등록")
    @ValidDistanceTest
    void addLineSectionInBaseDownStation(int distance) {
        // given
        지하찰_노선에_등록될_구간의_거리가_기존_역_사이_길이_보다_작다(distance);

        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, 정자역, distance);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작고,
     * When 새로운 역을 지하철 노선 중간에 등록할 경우,
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("새로운 역을 지하철 노선 중간에 등록할 경우, 지하철 노선에 구간을 등록")
    @ValidDistanceTest
    void addLineSectionInMiddlePosition(int distance) {
        // given
        지하찰_노선에_등록될_구간의_거리가_기존_역_사이_길이_보다_작다(distance);

        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 정자역, 양재역, distance);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * Given 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작고,
     * When 새로운 역을 지하철 노선 중간에 등록할 경우,
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("새로운 역을 지하철 노선 중간에 등록할 경우, 지하철 노선에 구간을 등록")
    @ValidDistanceTest
    void addLineSectionInMiddlePosition2(int distance) {
        // given
        지하찰_노선에_등록될_구간의_거리가_기존_역_사이_길이_보다_작다(distance);

        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 강남역, 정자역, distance);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * Given 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같고,
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가를 할 수 없음 (400 Bad Request HTTP/1.1)
     */
    @DisplayName("새로운 역을 지하철 노선 중간에 등록할 경우, 지하철 노선에 구간을 등록")
    @TooMuchDistanceTest
    void failToAddLineSectionInCaseOfTOoMuchDistance(int tooMuchDistance) {
        // given
        지하찰_노선에_등록될_구간의_거리가_기존_역_사이_길이_보다_같거나_크다(tooMuchDistance);

        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 강남역, 정자역, tooMuchDistance);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 상행역과 하행역이 이미 노선에 모두 등록되어 있고,
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가를 할 수 없음 (400 Bad Request HTTP/1.1)
     */
    @DisplayName("새로운 역을 지하철 노선 중간에 등록할 경우, 지하철 노선에 구간을 등록")
    @Test
    void failToAddLineSectionInCaseOfAlreadyExists() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 강남역, 정자역, 6);

        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 정자역, 신논현역, 2);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 신논현역, 양재역, 1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 상행역과 하행역 둘 중 하나도 포함되어있지 않고,
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가를 할 수 없음 (400 Bad Request HTTP/1.1)
     */
    @DisplayName("새로운 역을 지하철 노선 중간에 등록할 경우, 지하철 노선에 구간을 등록")
    @Test
    void failToAddLineSectionInCaseOfNotExists() {
        // given
        Long 정자역 = 1004L;
        Long 신논현역 = 1005L;

        지하철_노선에_지하철_역이_존재하지_않는다(신분당선, 정자역, 신논현역);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 정자역, 신논현역, 1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선에_지하철_역이_존재하지_않는다(Long lineId, Long... stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .doesNotContain(stationIds);
    }

    private void 지하찰_노선에_등록될_구간의_거리가_기존_역_사이_길이_보다_작다(int distance) {
        assertThat(distance).isStrictlyBetween(0, givenDistance);
    }

    private void 지하찰_노선에_등록될_구간의_거리가_기존_역_사이_길이_보다_같거나_크다(int distance) {
        assertThat(distance).isGreaterThanOrEqualTo(givenDistance);
    }
}
