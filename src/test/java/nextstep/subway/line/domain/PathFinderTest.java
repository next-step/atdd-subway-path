package nextstep.subway.line.domain;

import nextstep.subway.line.exception.NotExistPathInfoException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.EqualStationException;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {

    private Station 강남역;
    private Station 양재역;
    private Station 선릉역;
    private Station 도곡역;
    private Station 역삼역;
    private Station 매봉역;
    private Station 춘천역;
    private Station 강원역;
    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;
    private Line 분당선;
    private Line 춘천강원선;

    /**
     *   강남역 ---*2호선* --- 역삼역 ---*2호선* --- 선릉역
     *     |                                         |
     * *신분당선*                                  *분당선*
     *    |                                         |
     *  양재역 ---*3호선*--- 매봉역  ---*3호선*---  도곡역
     *
     *  춘천역 -----------*춘천강원선*------------- 강원역
     */
    @BeforeEach
    void setUp() {
        // given
        강남역 = initStation(강남역, "강남역", 1L);
        양재역 = initStation(양재역,"양재역", 2L);
        선릉역 = initStation(선릉역,"선릉역", 3L);
        도곡역 = initStation(도곡역,"도곡역", 4L);
        역삼역 = initStation(역삼역,"역삼역", 5L);
        매봉역 = initStation(매봉역,"매봉역", 6L);
        춘천역 = initStation(춘천역,"춘천역", 7L);
        강원역 = initStation(강원역,"강원역", 8L);

        이호선 = new Line("2호선", "green", 강남역, 선릉역, 20);
        삼호선 = new Line("3호선", "orange", 양재역, 도곡역, 20);
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, 7);
        분당선 = new Line("분당선", "yellow", 선릉역, 도곡역, 12);
        춘천강원선 = new Line("춘천강원선", "yellow", 춘천역, 강원역, 25);

        이호선.addSection(강남역, 역삼역, 10);
        삼호선.addSection(양재역, 매봉역, 10);
    }

    @DisplayName("최단 경로 정보를 가져온다.")
    @Test
    void getPathInfo() {
        // given
        PathFinder pathFinder = new PathFinder(asList(이호선, 삼호선, 신분당선, 분당선));

        // when
        StationGraph pathInfo = pathFinder.getPathInfo(역삼역, 도곡역);

        // then
        assertThat(pathInfo.getStations()).containsExactly(Arrays.array(역삼역, 선릉역, 도곡역));
        assertThat(pathInfo.getIntegerWeight()).isEqualTo(22);
    }

    @DisplayName("최단 경로 정보를 가져오다 실패한다 - case1 : 출발역과 도착역이 같은 경우")
    @Test
    void getPathInfoFailed1() {
        // given
        PathFinder pathFinder = new PathFinder(asList(이호선, 삼호선, 신분당선, 분당선));

        // when / then
        assertThatThrownBy(() -> {
            StationGraph pathInfo = pathFinder.getPathInfo(역삼역, 역삼역);
        }).isInstanceOf(EqualStationException.class);
    }

    @DisplayName("최단 경로 정보를 가져오다 실패한다 - case2 : 출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void getPathInfoFailed2() {
        // given
        PathFinder pathFinder = new PathFinder(asList(이호선, 삼호선, 신분당선, 분당선, 춘천강원선));

        // when / then
        assertThatThrownBy(() -> {
            StationGraph pathInfo = pathFinder.getPathInfo(강남역, 춘천역);
        }).isInstanceOf(NotExistPathInfoException.class);
    }

    @DisplayName("최단 경로 정보를 가져오다 실패한다 - case3 : 존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    void getPathInfoFailed3() {
        // given
        PathFinder pathFinder = new PathFinder(asList(이호선, 삼호선, 신분당선, 분당선));
        Station 모르는역1 = new Station("모르는역1");
        Station 모르는역2 = new Station("모르는역2");

        // when / then
        assertThatThrownBy(() -> {
            StationGraph pathInfo = pathFinder.getPathInfo(모르는역1, 모르는역2);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private Station initStation(Station station, String stationName, Long id) {
        station = new Station(stationName);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}
