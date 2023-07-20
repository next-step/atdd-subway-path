package subway.unit;

import subway.unit.fixture.StationLineSpec;
import subway.unit.fixture.StationSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Station;
import subway.domain.StationLine;
import subway.domain.StationLineSection;
import subway.domain.service.ShortestStationPath;
import subway.domain.service.StationShortestPathCalculateService;
import subway.exception.StationLineSearchFailException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static utils.UnitTestUtils.createEntityTestIds;

public class StationShortestPathCalculateServiceTest {
    StationShortestPathCalculateService stationShortestPathCalculateService = new StationShortestPathCalculateService();

    Map<String, Station> stationByName;
    List<Station> stations;
    List<StationLine> stationLines;
    List<StationLineSection> stationLineSections;

    @BeforeEach
    void setUp() {
        //given
        stations = StationSpec.of(List.of("A역", "B역", "C역", "D역", "E역", "F역", "G역", "H역", "I역", "Y역", "Z역"));
        stationByName = stations.stream()
                .collect(Collectors.toMap(Station::getName, Function.identity()));

        //A,B,C
        final StationLine line_1 = StationLineSpec.of(stationByName.get("A역"), stationByName.get("B역"), BigDecimal.valueOf(8L));
        line_1.createSection(stationByName.get("B역"), stationByName.get("C역"), BigDecimal.ONE);

        //C,D,E
        final StationLine line_2 = StationLineSpec.of(stationByName.get("C역"), stationByName.get("D역"), BigDecimal.valueOf(9L));
        line_2.createSection(stationByName.get("D역"), stationByName.get("E역"), BigDecimal.valueOf(7L));

        //E,F,G
        final StationLine line_3 = StationLineSpec.of(stationByName.get("E역"), stationByName.get("F역"), BigDecimal.valueOf(4L));
        line_3.createSection(stationByName.get("F역"), stationByName.get("G역"), BigDecimal.valueOf(3L));

        //G,H,I,A
        final StationLine line_4 = StationLineSpec.of(stationByName.get("G역"), stationByName.get("H역"), BigDecimal.ONE);
        line_4.createSection(stationByName.get("H역"), stationByName.get("I역"), BigDecimal.valueOf(7L));
        line_4.createSection(stationByName.get("I역"), stationByName.get("A역"), BigDecimal.valueOf(2L));

        //Y,Z
        final StationLine line_5 = StationLineSpec.of(stationByName.get("Y역"), stationByName.get("Z역"), BigDecimal.valueOf(4L));

        stationLines = List.of(line_1, line_2, line_3, line_4, line_5);
        createEntityTestIds(stationLines, 1L);

        stationLineSections = stationLines.stream()
                .map(StationLine::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        createEntityTestIds(stationLineSections, 1L);

        final List<StationLineSection> stationLineSections = stationLines.stream()
                .map(StationLine::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        createEntityTestIds(stationLineSections, 1L);
    }

    @DisplayName("정상적인 지하철 경로 조회")
    @Test
    void searchStationPath() {
        //when
        final ShortestStationPath path = stationShortestPathCalculateService.calculateShortestPath(
                stationByName.get("A역"),
                stationByName.get("E역"),
                stationLineSections);

        //then
        final BigDecimal expectedDistance = BigDecimal.valueOf(17);
        Assertions.assertEquals(0, expectedDistance.compareTo(path.getDistance()));

        final Map<Long, Station> stationById = stations.stream()
                .collect(Collectors.toMap(Station::getId, Function.identity()));

        final List<String> expectedPathStationNames = List.of("A역", "I역", "H역", "G역", "F역", "E역");
        final List<String> resultPathStationNames = path.getStationIds()
                .stream()
                .map(stationById::get)
                .map(Station::getName)
                .collect(Collectors.toList());

        Assertions.assertArrayEquals(expectedPathStationNames.toArray(), resultPathStationNames.toArray());
    }

    @DisplayName("출발역과 도착역 간의 경로가 존재하지 않는 경우 예외")
    @Test
    void searchStationPath_Not_Exists_Path_Between_SourceStation_TargetStation() {
        //when
        final Throwable throwable = Assertions.assertThrows(StationLineSearchFailException.class,
                () -> stationShortestPathCalculateService.calculateShortestPath(
                        stationByName.get("A역"),
                        stationByName.get("Z역"),
                        stationLineSections));

        //then
        Assertions.assertEquals("there is no path between start station and destination station", throwable.getMessage());
    }
}
