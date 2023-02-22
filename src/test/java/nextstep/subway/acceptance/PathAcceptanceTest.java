package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.경로_조회가_성공한다;
import static nextstep.subway.acceptance.PathSteps.경로에_역이_순서대로_포함되어있다;
import static nextstep.subway.acceptance.PathSteps.경로의_총_거리가_일치한다;
import static nextstep.subway.acceptance.PathSteps.지하철_경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.fixture.FieldFixture.식별자_아이디;
import static nextstep.subway.fixture.LineFixture.삼호선;
import static nextstep.subway.fixture.LineFixture.신분당선;
import static nextstep.subway.fixture.LineFixture.이호선;
import static nextstep.subway.fixture.SectionFixture.강남_양재_구간;
import static nextstep.subway.fixture.SectionFixture.교대_강남_구간;
import static nextstep.subway.fixture.SectionFixture.교대_남부터미널_구간;
import static nextstep.subway.fixture.SectionFixture.남부터미널_양재_구간;
import static nextstep.subway.fixture.StationFixture.강남역;
import static nextstep.subway.fixture.StationFixture.교대역;
import static nextstep.subway.fixture.StationFixture.남부터미널역;
import static nextstep.subway.fixture.StationFixture.양재역;
import static nextstep.subway.utils.JsonPathUtil.Long으로_추출;

@DisplayName("지하철 경로 검색 인수 테스트")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역_id;
    private Long 강남역_id;
    private Long 양재역_id;
    private Long 남부터미널역_id;

    /**
     *                  (10)
     *   교대역    ---- *2호선*  ---   강남역
     *     |                           |
     *    (2)                         (8)
     *   *3호선*                    *신분당선*
     *     |                          |
     * 남부터미널역   --- *3호선* ---   양재
     *                  (3)
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역_id = Long으로_추출(지하철역_생성_요청(교대역.역_이름()), 식별자_아이디);
        강남역_id = Long으로_추출(지하철역_생성_요청(강남역.역_이름()), 식별자_아이디);
        양재역_id = Long으로_추출(지하철역_생성_요청(양재역.역_이름()), 식별자_아이디);
        남부터미널역_id = Long으로_추출(지하철역_생성_요청(남부터미널역.역_이름()), 식별자_아이디);

        지하철_노선_생성_요청(이호선.생성_요청_데이터_생성(교대역_id, 강남역_id, 교대_강남_구간.구간_거리()));
        지하철_노선_생성_요청(신분당선.생성_요청_데이터_생성(강남역_id, 양재역_id, 강남_양재_구간.구간_거리()));
        Long 삼호선_id = Long으로_추출(
                지하철_노선_생성_요청(삼호선.생성_요청_데이터_생성(교대역_id, 남부터미널역_id, 교대_남부터미널_구간.구간_거리())),
                식별자_아이디);

        지하철_노선에_지하철_구간_생성_요청(삼호선_id, 남부터미널_양재_구간.요청_데이터_생성(남부터미널역_id, 양재역_id));
    }


    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 경로_탐색 {

        @Nested
        @DisplayName("출발역 id와 도착역 id로 지하철 경로 조회을 요청하면")
        class Context_with_find_path {

            @Test
            @DisplayName("출발역으로부터 도착역까지의 최단 경로의 역 목록과 총 구간 거리가 반환된다")
            void it_returns_total_distance_and_all_stations_on_route() throws Exception {
                ExtractableResponse<Response> 지하철_경로_조회_결과 = 지하철_경로_조회_요청(교대역_id, 양재역_id);

                경로_조회가_성공한다(지하철_경로_조회_결과);
                경로에_역이_순서대로_포함되어있다(지하철_경로_조회_결과, 교대역_id, 남부터미널역_id, 양재역_id);
                경로의_총_거리가_일치한다(지하철_경로_조회_결과, 교대_남부터미널_구간.구간_거리() + 남부터미널_양재_구간.구간_거리());
            }
        }
    }
}
