package nextstep.subway.unit;

import nextstep.subway.applicaion.JGraphPathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.global.error.exception.ErrorCode;
import nextstep.subway.global.error.exception.InvalidValueException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
    private List<Line> allLines = new ArrayList<>();

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
        allLines.addAll(List.of(lineTwo, lineThree, lineShinbundang));
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

    @DisplayName("출발역과 도착역이 연결되지 않는 경우 예외 발생")
    @Test
    void findNotConnectedStationException() {
        //given
        Station songpaStation = new Station("송파역");
        Station seoulStation = new Station("서울역");
        Line lineFour = new Line("4호선", "yellow");
        lineFour.addSection(songpaStation, seoulStation, 10);
        allLines.add(lineFour);

        //then
        Assertions.assertThatThrownBy(() -> {
                    pathFinder.find(allLines, gangnamStation, songpaStation);
                }).isInstanceOf(InvalidValueException.class)
                .hasMessage(ErrorCode.STATIONS_NOT_CONNECTED.getErrorMessage());
    }

    @DisplayName("출발역과 도착역이 같은 경우 예외 발생")
    @Test
    void findSameStationException() {
        //then
        Assertions.assertThatThrownBy(() -> {
                    pathFinder.find(allLines, gangnamStation, gangnamStation);
                }).isInstanceOf(InvalidValueException.class)
                .hasMessage(ErrorCode.START_STATION_MUST_NOT_SAME_END_STATION.getErrorMessage());
    }

    @DisplayName("출발역과 도착역이 노선에 없는 경우 예외 발생")
    @Test
    void findNotExistsStationException() {
        //given
        Station songpaStation = new Station("송파역");

        //then
        Assertions.assertThatThrownBy(() -> {
                    pathFinder.find(allLines, gangnamStation, songpaStation);
                }).isInstanceOf(InvalidValueException.class)
                .hasMessage(ErrorCode.NOT_EXISTS_STATIONS.getErrorMessage());
    }
}
