package atdd.line.domain;

import atdd.station.domain.Station;
import atdd.station.domain.StationTest;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineStationTest {

    private final Line line = LineTest.create(123L, "lineName!!", new TimeTable(LocalTime.MIN, LocalTime.MIDNIGHT), 0);
    private final Station station = StationTest.create(634L, "stationName!!");

    @Test
    void create() {
        final LineStation lineStation = new LineStation(line, station);

        assertThat(lineStation.getLine().getName()).isEqualTo(line.getName());
        assertThat(lineStation.getStation().getName()).isEqualTo(station.getName());
    }

    @Test
    void createByNullLine() throws Exception {
        final Line nullLine = null;

        assertThatThrownBy(() -> new LineStation(nullLine, station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("line 은 필수값 입니다.");
    }

    @Test
    void createByNullStation() {
        final Station nullStation = null;

        assertThatThrownBy(() -> new LineStation(line, nullStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("station 은 필수값 입니다.");
    }

    @Test
    void isEqualStation() {
        final LineStation lineStation = new LineStation(line, station);

        assertThat(lineStation.isEqualStation(station.getName())).isTrue();
        assertThat(lineStation.isEqualStation(line.getName())).isFalse();
    }

    @Test
    void isEqualLine() {
        final LineStation lineStation = new LineStation(line, station);

        assertThat(lineStation.isEqualLine(line.getName())).isTrue();
        assertThat(lineStation.isEqualLine(station.getName())).isFalse();
    }

}