package atdd.station.domain;

import atdd.line.domain.Line;
import atdd.line.domain.TimeTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationTest {

    private String name = "name!!";

    @Test
    void create()  {
        Station station = Station.create(name);

        assertThat(station.getName()).isEqualTo(name);
    }

    @DisplayName("name 은 빈값이나 null 일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createWithNullAndEmptyName(String name) {
        assertThatThrownBy(() -> Station.create(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name은 필수 입니다.");
    }

    @Test
    void addLine() {
        Station station = Station.of(143L, name);

        Line line = Line.of(654L, "line!!!", TimeTable.MAX_INTERVAL_TIME_TABLE, 0);

        station.addLine(line);

        final List<Line> lines = station.getLines();

        assertThat(lines).hasSize(1);

        final Line addedLine = lines.get(0);
        assertThat(addedLine.getId()).isEqualTo(line.getId());
        assertThat(addedLine.getName()).isEqualTo(line.getName());
    }

    @DisplayName("addLine - 동일한 이름의 line 을 중복 추가시 에러")
    @Test
    void addLineBySameName() {
        Station station = Station.of(143L, name);

        Line line = Line.create("line!!!", TimeTable.MAX_INTERVAL_TIME_TABLE, 0);

        station.addLine(line);

        assertThatThrownBy(() -> station.addLine(line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록된 line 입니다. lineName : [line!!!]");
    }

}