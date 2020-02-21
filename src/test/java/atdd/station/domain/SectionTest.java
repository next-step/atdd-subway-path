package atdd.station.domain;

import atdd.line.domain.Line;
import atdd.line.domain.LineTest;
import atdd.line.domain.TimeTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    private final Line line = LineTest.create(43L, "lineName!!", TimeTable.MAX_INTERVAL_TIME_TABLE, 0);
    private final Station station = StationTest.create(24L, "stationName111");
    private final Station nextStation = StationTest.create(25L, "stationName222");
    private final Duration duration = new Duration(LocalTime.MAX);
    private final int distance = 1;

    @Test
    void create() throws Exception {
        final Section section = Section.create(line, station, nextStation, duration, distance);

        assertThat(section.getLine()).isEqualTo(line);
        assertThat(section.getStation()).isEqualTo(station);
        assertThat(section.getNextStation()).isEqualTo(nextStation);
        assertThat(section.getDuration()).isEqualTo(duration);
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    @Test
    void createNullLine() throws Exception {
        final Line nullLine = null;

        assertThatThrownBy(() -> Section.create(nullLine, station, nextStation, duration, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("line 은 필수값 입니다.");
    }

    @Test
    void createNullStation() throws Exception {
        final Station nullStation = null;

        assertThatThrownBy(() -> Section.create(line, nullStation, nextStation, duration, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("station 은 필수값 입니다.");
    }

    @Test
    void createNullNextStation() throws Exception {
        final Station nullNextStation = null;

        assertThatThrownBy(() -> Section.create(line, station, nullNextStation, duration, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nextStation 은 필수값 입니다.");
    }

    @Test
    void createNullDuration() throws Exception {
        Duration nullDuration = null;

        assertThatThrownBy(() -> Section.create(line, station, nextStation, nullDuration, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration 은 필수값 입니다.");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -0.1})
    void createZeroDistance(double distance) throws Exception {

        assertThatThrownBy(() -> Section.create(line, station, nextStation, duration, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("distance 는 0보다 커야 합니다.");
    }

    @Test
    void isEqualLine() throws Exception {
        Section section = Section.create(line, station, nextStation, duration, distance);

        assertThat(section.isEqualLine(line.getName())).isTrue();
        assertThat(section.isEqualLine(line.getName() + "not")).isFalse();
    }

    @Test
    void isEqualNextSection() throws Exception {
        Section section = Section.create(line, station, nextStation, duration, distance);

        assertThat(section.isEqualNextSection(line.getName(), nextStation.getName())).isTrue();
        assertThat(section.isEqualNextSection(line.getName(), station.getName())).isFalse();
    }

}