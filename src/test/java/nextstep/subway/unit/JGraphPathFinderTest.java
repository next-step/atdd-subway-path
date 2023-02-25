package nextstep.subway.unit;

import nextstep.subway.applicaion.JGraphPathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class JGraphPathFinderTest {
    private Station gyodaeStation;
    private Station gangnamStation;
    private Station yangjaeStation;
    private Station nambuTerminalStation;
    private Line lineTwo;
    private Line lineShinbundang;
    private Line lineThree;
    private List<Line> allLines;

    private JGraphPathFinder pathFinder = new JGraphPathFinder();

    @BeforeEach
    void setUp() {
        gyodaeStation = new Station("교대역");
        gangnamStation = new Station("강남역");
        yangjaeStation = new Station("양재역");
        nambuTerminalStation = new Station("남부터미널역");
        lineTwo = new Line("2호선", "green");
        lineTwo.addSection(gyodaeStation, gangnamStation, 10);
        lineShinbundang = new Line("신분당선", "red");
        lineShinbundang.addSection(gangnamStation, yangjaeStation, 10);
        lineThree = new Line("3호선", "orange");
        lineThree.addSection(gyodaeStation, nambuTerminalStation, 2);
        lineThree.addSection(nambuTerminalStation, yangjaeStation, 3);
        allLines = List.of(lineTwo, lineThree, lineShinbundang);
    }

    @DisplayName("최단거리 경로조회")
    @Test
    void find() {
        //when
        PathResponse pathResponse = pathFinder.find(allLines, gangnamStation, nambuTerminalStation);

        //then
        List<String> stationNames = pathResponse.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        Assertions.assertThat(stationNames).containsExactly("강남역", "교대역", "남부터미널역");
        Assertions.assertThat(pathResponse.getDistance()).isEqualTo(12);
    }
}
