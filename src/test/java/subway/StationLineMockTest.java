package subway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Station;
import subway.domain.StationLine;
import subway.exception.StationLineSectionCreateException;
import subway.exception.StationLineSectionDeleteException;

import java.math.BigDecimal;
import java.util.List;

import static utils.UnitTestUtils.createEntityTestId;
import static utils.UnitTestUtils.createEntityTestIds;

public class StationLineMockTest {

    @DisplayName("정상적인 노선의 역 사이에 구간의 추가")
    @Test
    void createStationLineSection_Between_LineStations() {
        //given
        final Station lineUpStation = new Station("lineUpStation");
        final Station lineDownStation = new Station("lineDownStation");
        final Station sectionDownStation = new Station("newStation");

        createEntityTestIds(List.of(lineUpStation, lineDownStation, sectionDownStation), 1L);

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(lineUpStation)
                .downStation(lineDownStation)
                .distance(BigDecimal.TEN)
                .build();

        createEntityTestId(line, 1L);
        createEntityTestIds(line.getSections(), 1L);

        //when
        Assertions.assertDoesNotThrow(() -> line.createSection(lineUpStation, sectionDownStation, BigDecimal.ONE));

        //then
        final List<Station> expectedLineStations = List.of(lineUpStation, sectionDownStation, lineDownStation);
        Assertions.assertArrayEquals(expectedLineStations.toArray(), line.getAllStations().toArray());

        Assertions.assertEquals(BigDecimal.ONE, line.getSections().get(0).getDistance());
        Assertions.assertEquals(BigDecimal.valueOf(9), line.getSections().get(1).getDistance());
    }

    @DisplayName("새로운 구간의 길이가 기존 노선의 역 구간 거리보다 크거나 같은 경우 애러")
    @Test
    void createStationLineSection_Between_LineStations_largeThan_Exising_Distance() {
        //given
        final Station lineUpStation = new Station("lineUpStation");
        final Station lineDownStation = new Station("lineDownStation");
        final Station sectionDownStation = new Station("newStation");

        createEntityTestIds(List.of(lineUpStation, lineDownStation, sectionDownStation), 1L);

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(lineUpStation)
                .downStation(lineDownStation)
                .distance(BigDecimal.valueOf(5L))
                .build();

        createEntityTestId(line, 1L);
        createEntityTestIds(line.getSections(), 1L);

        //when
        final Throwable throwable = Assertions.assertThrows(StationLineSectionCreateException.class,
                () -> line.createSection(lineUpStation, sectionDownStation, BigDecimal.TEN));

        Assertions.assertEquals("new section distance must be less than existing section distance", throwable.getMessage());
    }

    @DisplayName("정상적인 구간의 하행역이 노선의 상행종점역인 구간 추가")
    @Test
    void createStationLineSection_Section_DownStation_Equals_To_Line_UpStation() {
        //given
        final Station lineUpStation = new Station("lineUpStation");
        final Station lineDownStation = new Station("lineDownStation");
        final Station sectionUpStation = new Station("newStation");

        createEntityTestIds(List.of(lineUpStation, lineDownStation, sectionUpStation), 1L);

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(lineUpStation)
                .downStation(lineDownStation)
                .distance(BigDecimal.ONE)
                .build();

        createEntityTestId(line, 1L);
        createEntityTestIds(line.getSections(), 1L);

        //when
        Assertions.assertDoesNotThrow(() -> line.createSection(sectionUpStation, lineUpStation, BigDecimal.TEN));

        //then
        final List<Station> expectedLineStations = List.of(sectionUpStation, lineUpStation, lineDownStation);
        Assertions.assertArrayEquals(expectedLineStations.toArray(), line.getAllStations().toArray());
    }

    @DisplayName("추가하려는 구간의 상행역이 노선의 하행종점역이 아닌 경우 예외 발생")
    @Test
    void createStationLineSection_sectionUpStation_NotEquals_To_LineLastDownStation() {
        //given
        final Station aStation = new Station("aStation");
        final Station bStation = new Station("bStation");
        final Station cStation = new Station("cStation");
        final Station dStation = new Station("dStation");
        final Station eStation = new Station("eStation");

        createEntityTestIds(List.of(aStation, bStation, cStation, dStation, eStation), 1L);

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(aStation)
                .downStation(bStation)
                .distance(BigDecimal.TEN)
                .build();

        line.createSection(bStation, cStation, BigDecimal.ONE);

        //when & then
        final Throwable throwable = Assertions.assertThrows(StationLineSectionCreateException.class,
                () -> line.createSection(dStation, eStation, BigDecimal.ONE));

        Assertions.assertEquals("section up station must be equals to line last down station", throwable.getMessage());
    }

    @DisplayName("추가하려는 구간의 하행역이 이미 노선의 역목록에 포함이된 경우 예외발생")
    @Test
    void createStationLineSection_sectionDownStation_Exist_To_LineStations() {
        //given
        final Station aStation = new Station("aStation");
        final Station bStation = new Station("bStation");
        final Station cStation = new Station("cStation");
        final Station dStation = new Station("dStation");

        createEntityTestIds(List.of(aStation, bStation, cStation, dStation), 1L);

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(aStation)
                .downStation(bStation)
                .distance(BigDecimal.TEN)
                .build();

        line.createSection(bStation, cStation, BigDecimal.ONE);

        //when & then
        final Throwable throwable = Assertions.assertThrows(StationLineSectionCreateException.class,
                () -> line.createSection(cStation, bStation, BigDecimal.ONE));

        Assertions.assertEquals("section down station must not be included to line station", throwable.getMessage());
    }


    @DisplayName("정상적인 노선의 구간 삭제")
    @Test
    void deleteStationLineSection() {
        //given
        final Station aStation = new Station("aStation");
        final Station bStation = new Station("bStation");
        final Station cStation = new Station("cStation");

        createEntityTestIds(List.of(aStation, bStation, cStation), 1L);

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(aStation)
                .downStation(bStation)
                .distance(BigDecimal.TEN)
                .build();

        line.createSection(bStation, cStation, BigDecimal.ONE);

        createEntityTestId(line, 1L);
        createEntityTestIds(line.getSections(), 1L);

        //when
        Assertions.assertDoesNotThrow(() -> line.deleteSection(cStation));

        //then
        final Station lineLastDownStation = line.getLineLastDownStation();

        Assertions.assertEquals(bStation, lineLastDownStation);
        Assertions.assertFalse(line.getAllStations().contains(cStation));
    }

    @DisplayName("삭제하려는 구간의 역이 지하철 노선의 하행 종점역이 아닌 경우 애러")
    @Test
    void deleteStationLineSection_Not_lastDownStation() {
        //given
        final Station aStation = new Station("aStation");
        final Station bStation = new Station("bStation");
        final Station cStation = new Station("cStation");

        createEntityTestIds(List.of(aStation, bStation, cStation), 1L);

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(aStation)
                .downStation(bStation)
                .distance(BigDecimal.TEN)
                .build();

        line.createSection(bStation, cStation, BigDecimal.ONE);

        createEntityTestId(line, 1L);
        createEntityTestIds(line.getSections(), 1L);

        //when & then
        final Throwable throwable = Assertions.assertThrows(StationLineSectionDeleteException.class,
                () -> line.deleteSection(bStation));

        Assertions.assertEquals("target section must be last station of line", throwable.getMessage());
    }


    @DisplayName("삭제하려는 구간의 지하철 노선이 2개의 역만 가진 노선일 경우 애러")
    @Test
    void deleteStationLineSection_has_2_StationLine() {
        //given
        final Station aStation = new Station("aStation");
        final Station bStation = new Station("bStation");

        createEntityTestIds(List.of(aStation, bStation), 1L);

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(aStation)
                .downStation(bStation)
                .distance(BigDecimal.TEN)
                .build();

        createEntityTestId(line, 1L);
        createEntityTestIds(line.getSections(), 1L);

        //when & then
        final Throwable throwable = Assertions.assertThrows(StationLineSectionDeleteException.class,
                () -> line.deleteSection(bStation));

        Assertions.assertEquals("section must be greater or equals than 2", throwable.getMessage());
    }
}
