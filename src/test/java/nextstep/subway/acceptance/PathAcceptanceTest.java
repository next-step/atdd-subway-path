package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.steps.LineSteps.*;
import static nextstep.subway.acceptance.steps.PathSteps.*;
import static nextstep.subway.acceptance.steps.StationSteps.역_생성_ID_추출;
import static nextstep.subway.acceptance.steps.StationSteps.지하철역_생성_요청;
import static nextstep.subway.fixture.LineFixture.*;
import static nextstep.subway.fixture.StationFixture.*;
import static nextstep.subway.utils.CustomAssertions.상태코드_확인;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역_ID;
    private Long 강남역_ID;
    private Long 양재역_ID;
    private Long 남부터미널역_ID;
    private Long 이호선_ID;
    private Long 신분당선_ID;
    private Long 삼호선_ID;

    /**
     * 노선 옆에 "()"는 구간의 거리를 의미합니다.
     * <p>
     * 교대역    --- *2호선*(3) ---   강남역
     * |                           |
     * *3호선*(4)                *신분당선*(5)
     * |                            |
     * 남부터미널역  --- *3호선*(2) ---   양재역
     */
    @BeforeEach
    void setup() {
        super.setUp();
        // given 역이 생성되어있다.
        교대역_ID = 역_생성_ID_추출(지하철역_생성_요청(교대역_이름));
        강남역_ID = 역_생성_ID_추출(지하철역_생성_요청(강남역_이름));
        양재역_ID = 역_생성_ID_추출(지하철역_생성_요청(양재역_이름));
        남부터미널역_ID = 역_생성_ID_추출(지하철역_생성_요청(남부터미널역_이름));
        // given 노선이 생성되어있다.
        이호선_ID = 노선_생성_ID_추출(지하철_노선_생성_요청(이호선_이름, 이호선_색));
        신분당선_ID = 노선_생성_ID_추출(지하철_노선_생성_요청(신분당선_이름, 신분당선_색));
        삼호선_ID = 노선_생성_ID_추출(지하철_노선_생성_요청(삼호선_이름, 삼호선_색));
        // given 각 노선에 구간이 들어가있다.
        지하철_노선에_지하철_구간_생성_요청(이호선_ID, 강남역_ID, 교대역_ID, 3);
        지하철_노선에_지하철_구간_생성_요청(삼호선_ID, 교대역_ID, 남부터미널역_ID, 4);
        지하철_노선에_지하철_구간_생성_요청(삼호선_ID, 남부터미널역_ID, 양재역_ID, 2);
        지하철_노선에_지하철_구간_생성_요청(신분당선_ID, 양재역_ID, 강남역_ID, 5);
    }

    /**
     * When 경로를 탐색하면
     * Then 최단 경로의 역 목록과 총 거리를 찾을 수 있다
     */
    @DisplayName("출발역과 도착역을 입력하였을 때 최단 경로의 역 목록 및 총 거리 조회")
    @Test
    void findPath() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_탐색_요청(교대역_ID, 양재역_ID);

        // then
        상태코드_확인(response, HttpStatus.OK);
        Assertions.assertThat(경로_탐색_역_ID_목록_추출(response)).containsExactly(교대역_ID, 남부터미널역_ID, 양재역_ID);
        Assertions.assertThat(경로_탐색_최단거리_추출(response)).isEqualTo(4 + 2);
    }

    /**
     * When 출발역과 도착역을 같게 경로를 탐색하면
     * Then 경로 탐색에 실패한다
     */
    @DisplayName("출발역과 도착역이 같을 경우 경로조회에 실패")
    @Test
    void findPathEqualStation() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_탐색_요청(교대역_ID, 교대역_ID);
        // then
        상태코드_확인(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 연결되지 않은 역,노선,구간을 추가한다.
     * When 출발역과 도착역에 연결되지 않은 역을 넣는다.
     * Then 경로 탐색에 실패한다
     */
    @DisplayName("출발역과 도착역이 연결되어 있지 않는 경우 경로조회에 실패")
    @Test
    void findPathDisConnectedStation() {
        // given 연결되지 않은 역,노선,구간을 추가한다.
        Long 복정역_ID = 역_생성_ID_추출(지하철역_생성_요청("복정역"));
        Long 수서역_ID = 역_생성_ID_추출(지하철역_생성_요청("수서역"));
        Long 분당선_ID = 노선_생성_ID_추출(지하철_노선_생성_요청(분당선_이름, 분당선_색));
        지하철_노선에_지하철_구간_생성_요청(복정역_ID, 수서역_ID, 분당선_ID, 3);
        // when
        ExtractableResponse<Response> response = 지하철_경로_탐색_요청(교대역_ID, 수서역_ID);
        // then
        상태코드_확인(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 존재하지 않는 역을 경로 탐색한다.
     * Then 경로 탐색에 실패한다
     */
    @DisplayName("존재하지 않는 역을 탐색하는 경우 경로조회에 실패")
    @Test
    void findPathNotExistsStation() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_탐색_요청(1000L, 1001L);
        // then
        상태코드_확인(response, HttpStatus.BAD_REQUEST);
    }
}
