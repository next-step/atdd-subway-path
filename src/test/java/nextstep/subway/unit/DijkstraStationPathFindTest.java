package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathResult;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.common.ErrorMessage;
import nextstep.subway.domain.DijkstraStationPathFind;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Dijkstra 경로 조회 테스트")
public class DijkstraStationPathFindTest {

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;
    Line 이호선;
    Line 삼호선;
    Line 신분당선;

    DijkstraStationPathFind 다익스트라맵;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */

    @BeforeEach
    void init() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");
        이호선 = new Line("이호선", "green");
        삼호선 = new Line("삼호선", "orange");
        신분당선 = new Line("신분당선", "red");

        이호선.addSections(교대역, 강남역, 10);
        삼호선.addSections(교대역, 남부터미널역, 2);
        삼호선.addSections(남부터미널역, 양재역, 3);
        신분당선.addSections(강남역, 양재역, 10);

        다익스트라맵 = new DijkstraStationPathFind();
        다익스트라맵.init(List.of(이호선, 삼호선, 신분당선));
    }

    @DisplayName("최단경로를 조회할 수 있다.")
    @Test
    void 최단경로_조회() {
        PathResult<Station> stationPathResult = 다익스트라맵.find(교대역, 양재역);

        List<Station> stationList = stationPathResult.getPathList();

        assertThat(stationList).containsExactly(교대역, 남부터미널역, 양재역);
    }

    @DisplayName("출발역과 도착역이 같은 경우 에러가 발생한다.")
    @Test
    void 출발역_도착역_같음_에러() {

        assertThatThrownBy(() -> 다익스트라맵.find(교대역, 교대역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.DUPLICATED_PATH_FIND.toString());
    }

    @DisplayName("출발역과 도착역이 연결되지 않으면 에러가 발생한다.")
    @Test
    void 출발역_도착역_연결되지않음_에러() {
        Station 신사역 = new Station("신사역");

        assertThatThrownBy(() -> 다익스트라맵.find(교대역, 신사역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("graph must contain the sink vertex");
    }
}
