package nextstep.subway.acceptance.path;

import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.line.dto.ShortestPathResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.line.LineTestUtils.*;
import static nextstep.subway.acceptance.path.PathTestUtils.*;
import static nextstep.subway.acceptance.station.StationTestUtils.*;

@DisplayName("지하철 경로 탐색")
public class PathAcceptanceTest extends AcceptanceTest {

    /**
     * 교대역 --- *2호선* --- 강남역
     * ㅣ                     ㅣ
     * *3호선*              *신분당선*
     * ㅣ                       ㅣ
     * 남부터미널역 --- *3호선* --- 양재역
     * */
    String 교대역_URL;
    String 강남역_URL;
    String 양재역_URL;
    String 남부터미널역_URL;
    String 익명역_URL;
    String 이호선_URL;
    String 신분당선_URL;
    String 삼호선_URL;

    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역_URL = 지하철역_생성(교대역_정보);
        강남역_URL = 지하철역_생성(강남역_정보);
        양재역_URL = 지하철역_생성(양재역_정보);
        남부터미널역_URL = 지하철역_생성(남부터미널역_정보);
        이호선_URL= 지하철_노선_생성(이호선_생성_요청, 교대역_URL, 강남역_URL, 10);
        신분당선_URL = 지하철_노선_생성(신분당선_생성_요청, 강남역_URL, 양재역_URL, 2);
        삼호선_URL = 지하철_노선_생성(삼호선_생성_요청, 교대역_URL, 남부터미널역_URL, 3);
        지하철_구간_등록(삼호선_URL, 남부터미널역_URL, 양재역_URL, 3);
    }

    @DisplayName("경로 조회 성공")
    @Test
    void findPath() {
        // when
        ShortestPathResponse 경로_조회_응답 = 지하철_최단_경로_조회(교대역_URL, 강남역_URL);

        // then
        경로_조회_결과는_다음과_같다(경로_조회_응답, 교대역_URL, 남부터미널역_URL, 양재역_URL, 강남역_URL);
        최단_경로_길이는_다음과_같다(경로_조회_응답, 8);
    }


    @Nested
    @DisplayName("경로 조회 실패")
    class findPathFailed {

        @DisplayName("출발 역과 도착 역이 같은 경우")
        @Test
        void sourceAndTargetStationIsSame() {
            지하철_최단_경로_조회_실패(교대역_URL, 교대역_URL);
        }

        @DisplayName("출발 역과 도착 역이 연결되있지 않은 경우")
        @Test
        void sourceAndTargetStationIsNotConnected() {
            익명역_URL = 지하철역_생성(익명역_정보);
            지하철_최단_경로_조회_실패(교대역_URL, 익명역_URL);
        }

        @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
        @Test
        void sourceOrTargetStationDoNotExist() {
            익명역_URL = "/stations/9999999";
            지하철_최단_경로_조회_실패(익명역_URL, 교대역_URL);
            지하철_최단_경로_조회_실패(교대역_URL, 익명역_URL);
        }
    }
}
