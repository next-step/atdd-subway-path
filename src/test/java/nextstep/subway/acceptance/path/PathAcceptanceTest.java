package nextstep.subway.acceptance.path;

import static nextstep.subway.acceptance.line.LineSteps.*;
import static nextstep.subway.acceptance.path.PathSteps.*;
import static nextstep.subway.acceptance.station.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;

@DisplayName("최단 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * Given 지하철 노선과 노선에 포함되는 지하철 역이 있을때
     *
     * 교대역     --- *2호선* --- 강남역
     * ㅣ                          ㅣ
     * *3호선*                  *신분당선*
     * ㅣ                          ㅣ
     * 남부터미널역 --- *3호선* --- 양재역
     *
     * 교대역 - 강남역 - 양재역 거리 = 20
     * 교대역 - 남부터미널역 - 양재역 거리 = 12
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청_하고_ID_반환("교대역");
        강남역 = 지하철역_생성_요청_하고_ID_반환("강남역");
        양재역 = 지하철역_생성_요청_하고_ID_반환("양재역");
        남부터미널역 = 지하철역_생성_요청_하고_ID_반환("남부터미널역");

        이호선 = 지하철_노선_생성_요청_하고_ID_반환("이호선", "green");
        신분당선 = 지하철_노선_생성_요청_하고_ID_반환("신분당선", "blue");
        삼호선 = 지하철_노선_생성_요청_하고_ID_반환("삼호선", "red");

        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(교대역, 강남역, 10));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 10));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(교대역, 남부터미널역, 2));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 10));
    }

    /**
     * When  출발 지하철역과 도착 지하철역을 포함해 경로 조회를 요청하면
     * Then  최단 거리를 조회한다.
     */
    @DisplayName("경로 조회")
    @Test
    void findShortestPaths() {
        // When
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 양재역);

        // Then
        System.out.println(response.jsonPath().getList("stations.id", Long.class));
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(12);
    }

    /**
     * When  출발역과 도착역이 같도록 경로 조회를 요청하면
     * Then  최단 거리 조회가 실패한다.
     */
    @DisplayName("경로 조회 - 출발역과 도착역이 같은 경우")
    @Test
    void findShortestPathsFailCase1() {
        // When
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 교대역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 다른 노선과 연결되어있지 않은 노선과 지하철 역이 존재할때
     * When  출발역고 도착역이 연결되어 있지 않은 경로로 조회를 요청하면
     * Then  최단 거리 조회가 실패한다.
     */
    @DisplayName("경로 조회 - 출발역과 도착역이 연결되어 있지 않은 경우")
    @Test
    void findShortestPathsFailCase2() {
        // Given
        Long 연결_안된_노선 = 지하철_노선_생성_요청_하고_ID_반환("연결 안된 노선", "BLACK");
        Long 새로운_상행 = 지하철역_생성_요청_하고_ID_반환("상행");
        Long 새로운_하행 = 지하철역_생성_요청_하고_ID_반환("하행");
        지하철_노선에_지하철_구간_생성_요청(연결_안된_노선, createSectionCreateParams(새로운_상행, 새로운_하행, 10));

        // When
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 새로운_하행);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When  출발역이나 도착역이 존재하지 않은 경로로 조회를 요청하면
     * Then  최단 거리 조회가 실패한다.
     */
    @DisplayName("경로 조회 - 존재하지 않는 출발역이나 도착역을 조회할 경우")
    @Test
    void findShortestPathsFailCase3() {
        // When
        Long 없는_출발역 = 111L;
        Long 없는_도착역 = 222L;
        ExtractableResponse<Response> response = 경로_조회_요청(없는_출발역, 없는_도착역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
