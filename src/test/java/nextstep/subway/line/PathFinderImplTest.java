package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathFinderImplTest {

    Station gangnamStation;
    Station yangjaeStation;
    Station dogokStation;
    Station suseoStation;
    Line shinbundangLine;
    Line line3;
    PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        pathFinder = new PathFinderImpl();
        gangnamStation = new Station("강남역");
        yangjaeStation = new Station("양재역");
        shinbundangLine = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        dogokStation = new Station("도곡역");
        suseoStation = new Station("수서역");
    }

    /**
     * 강남역    --- *2호선* --- 선릉역
     * |                        |
     * *신분당선*               *분당선*
     * |                        |
     * 양재역    --- *3호선* --- 도곡역  --- *3호선* ---수서역
     *                          |                  |
     *                              --- *분당선* ---
     */
    @DisplayName("출발역 부터 도착역까지의 최단거리를 찾는다")
    @Test
    void findShortestDistance() {
        // given
        line3 = new Line("3호선", "#82C341", yangjaeStation, dogokStation, 2);
        line3.addSection(dogokStation, suseoStation, 4);

        Station seolleungStation = new Station("선릉역");
        Line line2 = new Line("2호선", "#0052A4", gangnamStation, seolleungStation, 2);

        Line bundangLine = new Line("분당선", "#82C341", seolleungStation, dogokStation, 2);
        bundangLine.addSection(dogokStation, suseoStation, 4);

        List<Line> allLine = List.of(shinbundangLine, line2, line3, bundangLine);

        // when
        PathResponse pathResponse = pathFinder.findShortestDistance(gangnamStation, suseoStation, allLine);

        // then
        List<String> stationNames = pathResponse.getStations()
                .stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        Assertions.assertAll(
                () -> assertThat(stationNames).isEqualTo(List.of("강남역", "양재역", "도곡역", "수서역")),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(7)
        );
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 예외가 발생한다")
    @Test
    void findShortestPathBetweenStationsFailedByUnreachable() {
        // given
        line3 = new Line("3호선", "#82C341", dogokStation, suseoStation, 4);
        List<Line> allLine = List.of(shinbundangLine, line3);

        // when,then
        assertThatThrownBy(() -> pathFinder.findShortestDistance(gangnamStation, suseoStation, allLine))
                .isInstanceOf(UnreachableDestinationException.class);
    }
}
