package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.AssertResponse.응답_성공_검증;
import static nextstep.subway.acceptance.AssertResponse.응답_실패_검증;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.fixture.LineFixture.createLineCreateParams;
import static nextstep.subway.fixture.SectionFixture.createSectionCreateParams;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 관련 테스트")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 상도역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선*(10) ---   강남역
     * |                                |
     * *3호선*(2)                     *신분당선*(10)
     * |                                |
     * 남부터미널역  --- *3호선*(3) ---   양재
     * <p>
     * 상도역
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");
        상도역 = 지하철역_생성_요청("상도역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When  : 경로를 조회 하면
     * Then  : 경로가 조회 된다
     */
    @DisplayName("경로 조회 성공")
    @Test
    void findPath() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 양재역);

        // then
        응답_성공_검증(response);

        // 역 검증
        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);
        assertThat(stations).hasSize(3);

        List<Long> ids = stations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(ids).containsExactly(교대역, 남부터미널역, 양재역);

        // 거릭 검증
        int distance = response.jsonPath().getInt("distance");
        assertThat(distance).isEqualTo(5);
    }

    /**
     * When  : 출발역과 도착역이 같은 경로를 조회하면
     * Then  : 경로 조회에 실패 한다
     */
    @DisplayName("출발역과 도착역이 같은 경로는 조회 불가")
    @Test
    void findPathException1() {
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 교대역);

        응답_실패_검증(response);
    }


    /**
     * When  : 출발역과 도착역이 연결되지 않은 경로를 조회 하면
     * Then  : 경로 조회에 실패 한다
     */
    @DisplayName("출발역과 도착역이 연결되지 않은 경로 조회 불가")
    @Test
    void findPathException2() {
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 상도역);

        응답_실패_검증(response);
    }

    /**
     * When  : 출발역 또는 도착역이 없는 경로를 조회 하면
     * Then  : 경로 조회에 실패 한다
     */
    @DisplayName("출발역 또는 도착역이 없으면 경로 조회 불가")
    @Test
    void findPathException3() {
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 상도역);

        응답_실패_검증(response);
    }
}
