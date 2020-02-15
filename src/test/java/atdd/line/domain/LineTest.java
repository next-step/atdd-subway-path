package atdd.line.domain;

import atdd.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private final String name = "name!!";
    private final TimeTable timeTable = new TimeTable(LocalTime.MIN, LocalTime.MAX);
    private final int intervalTime = 1;

    @Test
    void create() throws Exception {
        final Line line = Line.create(name, timeTable, intervalTime);

        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getTimeTable()).isEqualTo(timeTable);
        assertThat(line.getIntervalTime()).isEqualTo(intervalTime);
    }

    @DisplayName("create - name 은 null 이거나 빈 값일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createByNullAndEmptyName(String nullAndEmptyName) {
        assertThatThrownBy(() -> Line.create(nullAndEmptyName, timeTable, intervalTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name 은 필수값 입니다.");
    }

    @DisplayName("create - timeTable 은 null 일 수 없다.")
    @Test
    void createByNullTimeTable() {
        final TimeTable nullTimeTable = null;

        assertThatThrownBy(() -> Line.create(name, nullTimeTable, intervalTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("timeTable 은 필수값 입니다.");
    }

    @DisplayName("create - timeTable 은 음수 일 수 없다.")
    @Test
    void createByNegativeIntervalTime() {
        final int negativeIntervalTime = -1;

        assertThatThrownBy(() -> Line.create(name, timeTable, negativeIntervalTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("intervalTime 은 음수일 수 없습니다.");
    }

    @DisplayName("addStation - 동일한 이름의 Station 추가시 에러")
    @Test
    void addSameNameStation() throws Exception {
        final Station station = Station.create("stationName!!");

        final Line line = Line.create(name, timeTable, intervalTime);

        line.addStation(station);
        assertThatThrownBy(() -> line.addStation(station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 역입니다. 역이름 : [stationName!!]");
    }

    @Test
    void getStations() {
        final Station station1 = Station.of(4341L, "stationName1!!");
        final Station station2 = Station.of(4341L, "stationName2!!");

        final Line line = Line.create(name, timeTable, intervalTime);
        line.addStation(station1);
        line.addStation(station2);


        final List<Station> stations = line.getStations();


        assertThat(stations).hasSize(2);
        assertThat(stations.get(0)).isEqualTo(station1);
        assertThat(stations.get(1)).isEqualTo(station2);
    }

}