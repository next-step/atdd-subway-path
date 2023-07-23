package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.PathSteps.경로_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 아이디_추출(지하철역_생성_요청("교대역"));
        강남역 = 아이디_추출(지하철역_생성_요청("강남역"));
        양재역 = 아이디_추출(지하철역_생성_요청("양재역"));
        남부터미널역 = 아이디_추출(지하철역_생성_요청("남부터미널역"));

        이호선 = 아이디_추출(지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10)));
        신분당선 = 아이디_추출(지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10)));
        삼호선 = 아이디_추출(지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2)));

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 지하철 경로를 조회하면
     * Then 경로가 조회된다
     */
    @DisplayName("지하철 경로 조회")
    @Test
    void getPath() {
        // when
        ExtractableResponse<Response> response = 경로_조회(교대역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철역_이름_리스트_추출(response)).containsExactly("교대역", "남부터미널역", "양재역");
        assertThat(거리_추출(response)).isEqualTo(5);
    }

    /**
     * When 동일한 출발역과 도착역으로 지하철 경로를 조회하면
     * Then 에러 발생한다
     */
    @DisplayName("동일한 출발역과 도착역으로 지하철 경로 조회")
    @Test
    void getPathBySameSourceAndTarget() {
        // when
        ExtractableResponse<Response> response = 경로_조회(교대역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 새로운 지하철역과 새로운 노선을 생성하고
     * When 연결되어 있지 않은 출발역과 도착역으로 지하철 경로를 조회하면
     * Then 에러 발생한다
     */
    @DisplayName("연결되어 있지 않은 출발역과 도착역으로 지하철 경로 조회")
    @Test
    void getPathByNotConnectedStations() {
        // given
        Long 고촌 = 아이디_추출(지하철역_생성_요청("고촌"));
        Long 김포공항 = 아이디_추출(지하철역_생성_요청("김포공항"));

        Long 김포골드 = 아이디_추출(지하철_노선_생성_요청(createLineCreateParams("김포골드", "gold", 고촌, 김포공항, 10)));

        // when
        ExtractableResponse<Response> response = 경로_조회(고촌, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given 새로운 지하철역을 생성하고
     * When 존재하지 않은 출발역으로 지하철 경로를 조회하면
     * Then 에러 발생한다
     */
    @DisplayName("존재하지 않은 출발역으로 지하철 경로 조회")
    @Test
    void getPathByNotExistingSourceStation() {
        // given
        Long noStationId = -1L;

        // when
        ExtractableResponse<Response> response = 경로_조회(noStationId, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given 새로운 지하철역을 생성하고
     * When 존재하지 않은 도착역으로 지하철 경로를 조회하면
     * Then 에러 발생한다
     */
    @DisplayName("존재하지 않은 도착역으로 지하철 경로 조회")
    @Test
    void getPathByNotExistingTargetStation() {
        // given
        Long noStationId = -1L;

        // when
        ExtractableResponse<Response> response = 경로_조회(교대역, noStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private Long 아이디_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    private List<String> 지하철역_이름_리스트_추출(ExtractableResponse<Response> response) {
        List<Station> stations = response.jsonPath().getList("stations", Station.class);
        return stations.stream().map(Station::getName).collect(Collectors.toList());
    }

    private int 거리_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getInt("distance");
    }
}
