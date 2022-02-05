package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.HttpRequestTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.LineStepUtil.*;
import static nextstep.subway.utils.SectionStepUtil.구간등록;
import static nextstep.subway.utils.StationStepUtil.지하철_역_아이디_키;
import static nextstep.subway.utils.StationStepUtil.지하철역생성;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest{

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    @BeforeEach
    public void setUp(){
        super.setUp();

        교대역 = 지하철역생성("교대역").jsonPath().getLong(지하철_역_아이디_키);
        강남역 = 지하철역생성("강남역").jsonPath().getLong(지하철_역_아이디_키);
        양재역 = 지하철역생성("양재역").jsonPath().getLong(지하철_역_아이디_키);
        남부터미널역 = 지하철역생성("남부터미널역").jsonPath().getLong(지하철_역_아이디_키);


    }

    /**             100
     * 교대역    --- *2호선* ---   강남역
     * |                        |
* 10 * *3호선*                   *신분당선*      10
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     *              50
     */

    /**
     *  Given 노선들을 등록한다
     *  When  최단 경로 조회를 요청한다
     *  Then  최단 경로를 응답한다.
     */
    @DisplayName("최단 경로를 조회한다")
    @Test
    void 최단_경로를_조회(){
        //given
        이호선 = 노선생성(노선파라미터생성("2호선", "green", 교대역, 강남역, 100)).jsonPath().getLong(노선_아이디_키);
        신분당선 = 노선생성(노선파라미터생성("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong(노선_아이디_키);
        삼호선 = 노선생성(노선파라미터생성("3호선", "orange", 교대역, 남부터미널역, 10)).jsonPath().getLong(노선_아이디_키);
        구간등록(삼호선, 남부터미널역, 양재역, 50);

        //when
        ExtractableResponse<Response> 최단_거리_응답 = 최단_경로_조회_요청(교대역, 양재역);

        //then
        단일_값_검사(최단_거리_응답, "distance", "60");
        리스트_값_검사(최단_거리_응답, "stations.id", Math.toIntExact(교대역), Math.toIntExact(양재역), Math.toIntExact(남부터미널역));
    }


    /**
     * 교대역                 강남역
     * |                        |
     * 10 * *3호선*                   *신분당선*      10
     * |                        |
     * 남부터미널역                양재
     *              50
     */

    /**
     * Given 노선들을 등록한다
     * When  연결되지 않은 역의 최단 경로 조회를 요청한다
     * Then  경로 탐색에 실패한다.
     */
    @DisplayName("최단 경로를 조회 시 출발지와 목적지는 연결되어 있어야한다")
    @Test
    void 출발역과_도착역이_연결되지_않는_경우_실패() {
        //given
        신분당선 = 노선생성(노선파라미터생성("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong(노선_아이디_키);
        삼호선 = 노선생성(노선파라미터생성("3호선", "orange", 교대역, 남부터미널역, 10)).jsonPath().getLong(노선_아이디_키);

        //when
        ExtractableResponse<Response> 최단_거리_응답 = 최단_경로_조회_요청(남부터미널역, 양재역);

        //then
        예외_검사(최단_거리_응답, "연결되지 않음");
    }

    /**
     * Given 노선들을 등록한다
     * When  연결되지 않은 역의 최단 경로 조회를 요청한다
     * Then  경로 탐색에 실패한다.
     */
    @DisplayName("출발역과 도착역이 같을 경우 실패")
    @Test
    void 출발역과_도착역이_같을_경우_실패() {
        //given
        이호선 = 노선생성(노선파라미터생성("2호선", "green", 교대역, 강남역, 100)).jsonPath().getLong(노선_아이디_키);
        신분당선 = 노선생성(노선파라미터생성("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong(노선_아이디_키);
        삼호선 = 노선생성(노선파라미터생성("3호선", "orange", 교대역, 남부터미널역, 10)).jsonPath().getLong(노선_아이디_키);
        구간등록(삼호선, 남부터미널역, 양재역, 50);

        //when
        ExtractableResponse<Response> 최단_거리_응답 = 최단_경로_조회_요청(남부터미널역, 남부터미널역);

        //then
        예외_검사(최단_거리_응답, "출발지와 도착지가 같습니다");
    }

    private ExtractableResponse<Response> 최단_경로_조회_요청(Long 출발역, Long 도착역) {
        return HttpRequestTestUtil.겟_요청("/paths?source=" + 출발역 + "&target=" + 도착역);
    }


}
