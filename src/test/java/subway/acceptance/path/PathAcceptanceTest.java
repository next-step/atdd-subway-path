package subway.acceptance.path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.acceptance.AcceptanceTest;
import subway.acceptance.line.LineFixture;
import subway.acceptance.line.LineSteps;
import subway.acceptance.line.SectionFixture;
import subway.acceptance.station.StationFixture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.acceptance.station.StationFixture.getStationId;

@DisplayName("경로 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    /**
     * <pre>
     * 교대역  ---- *2호선* --- d:10 ------  강남역
     * |                                    |
     * *3호선*                            *신분당선*
     * d:2                                 d:10
     * |                                   |
     * 남부터미널역  --- *3호선* -- d:3 --- 양재역
     *
     * 건대역 ---- *A호선* --- d:7 ---- 성수역 ---- d:3 ---- 왕십리역
     * </pre>
     */

    @BeforeEach
    void createLine() {
        StationFixture.기본_역_생성();

        이호선_삼호선_신분당선_A호선_생성();

        LineSteps.노선_목록_조회_API();
    }

    /**
     * Given 2개의 구간을 가진 노선이 있고
     * When 노선의 상행역과 하행역으로 경로를 조회하면
     * Then 3개의 역이 출력된다
     * Then 2 구간의 모든 거리의 합이 출력된다
     */
    @DisplayName("같은 노선의 경로를 조회한다")
    @Test
    void getPath() {
        // when
        var response = PathSteps.getShortestPath(getStationId("교대역"), getStationId("양재역"));

        // then
        List<String> list = response.jsonPath().getList("stations.name", String.class);
        assertThat(list).containsExactlyInAnyOrder("교대역", "남부터미널역", "양재역");

        // then
        var distance = response.jsonPath().get("distance");
        assertThat(distance).isEqualTo(5);
    }

    /**
     * Given 3개의 서로 연결된 노선이 있고
     * When 다른 노선을 모두 통과하는 경로를 조회하면
     * Then 경로 조회 결과가 나온다
     * Then 구간의 모든 거리의 합이 출력된다
     */
    @DisplayName("다른 노선에 있는 지하철 경로를 조회한다")
    @Test
    void getPathWithOtherLine() {
        // when
        var response = PathSteps.getShortestPath(getStationId("강남역"), getStationId("남부터미널역"));

        // then
        List<String> list = response.jsonPath().getList("stations.name", String.class);
        assertThat(list).containsExactlyInAnyOrder("강남역", "교대역", "남부터미널역");

        // then
        var distance = response.jsonPath().get("distance");
        assertThat(distance).isEqualTo(12);
    }

    /**
     * Given 3개의 서로 연결된 노선이 있고
     * When 연결되지 않은 경로를 조회하면
     * Then 경로가 조회되지 않는다
     */
    @DisplayName("연결되지 않은 경로를 조회한다")
    @Test
    void getPathWithNotConnected() {
        // when
        var response = PathSteps.getShortestPath(getStationId("교대역"), getStationId("왕십리역"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 3개의 서로 연결된 노선이 있고
     * When 시작과 끝을 같은 역을 조회하면
     * Then 경로가 조회되지 않는다
     */
    @DisplayName("시작과 끝이 같은 역의 경로를 조회한다.")
    @Test
    void getPathWithSameStation() {
        // when
        var response = PathSteps.getShortestPath(getStationId("강남역"), getStationId("강남역"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());


    }

    /**
     * Given 3개의 서로 연결된 노선이 있고
     * When 존재 하지 않는 역으로 경로를 조회하면
     * Then 경로가 조회되지 않는다
     */
    @DisplayName("존재하지 않는 역으로 경로를 조회한다")
    @Test
    void getPathWithNotExistStation() {
        // when
        var response = PathSteps.getShortestPath(99L, 98L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    private void 이호선_삼호선_신분당선_A호선_생성() {
        var 이호선_요청 = LineFixture.generateLineCreateRequest("2호선", "bg-green-600", getStationId("강남역"), getStationId("교대역"), 10L);
        LineSteps.노선_생성_API(이호선_요청);

        var 삼호선_요청 = LineFixture.generateLineCreateRequest("3호선", "bg-amber-600", getStationId("교대역"), getStationId("남부터미널역"), 2L);
        var 삼호선_응답 = LineSteps.노선_생성_API(삼호선_요청);
        var 삼호선_URI = 삼호선_응답.header("Location");

        var 삼호선_끝에_구간_추가 = SectionFixture.구간_요청_만들기(getStationId("남부터미널역"), getStationId("양재역"), 3L);
        LineSteps.구간_추가_API(삼호선_URI, 삼호선_끝에_구간_추가);

        var 신분당선_요청 = LineFixture.generateLineCreateRequest("신분당선", "bg-hotpink-600", getStationId("강남역"), getStationId("양재역"), 10L);
        LineSteps.노선_생성_API(신분당선_요청);

        var A호선_요청 = LineFixture.generateLineCreateRequest("A호선", "bg-black-600", getStationId("건대역"), getStationId("성수역"), 7L);
        var A호선_응답 = LineSteps.노선_생성_API(A호선_요청);
        var A호선_URI = A호선_응답.header("Location");

        var A호선_끝에_구간_추가 = SectionFixture.구간_요청_만들기(getStationId("성수역"), getStationId("왕십리역"), 3L);
        LineSteps.구간_추가_API(A호선_URI, 삼호선_끝에_구간_추가);
        LineSteps.구간_추가_API(삼호선_URI, A호선_끝에_구간_추가);
    }
}
