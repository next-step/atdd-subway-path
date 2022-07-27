package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    Station 기흥역;
    Station 신갈역;
    Station 정자역;
    Line line;

    @BeforeEach
    void setUp() {
        기흥역 = new Station(1L, "기흥역");
        신갈역 = new Station(2L, "신갈역");
        정자역 = new Station(3L, "정자역");
        line = new Line(4L, "분당선", "yellow");
        line.addSection(기흥역, 신갈역, 10);
        line.addSection(신갈역, 정자역, 10);
    }

    @Test
    void addSection() {
        // then
        assertAll(
            () -> assertThat(line.getSections().size()).isEqualTo(2),
            () -> assertThat(line.getSections().getStationNames()).containsExactlyInAnyOrder("기흥역", "신갈역", "정자역")
                 );
    }

    @Test
    void getStations() {
        // when
        List<Station> stations = line.getSections().getStations();

        // then
        assertAll(
            () -> assertThat(stations).containsExactlyInAnyOrder(기흥역, 신갈역, 정자역),
            () -> assertThat(stations).hasSize(3)
                 );
    }

    @Test
    void removeSection() {
        // when
        line.getSections().removeSection(정자역.getId());

        // then
        assertThat(line.getSections().getStationNames()).doesNotContain("정자역");
    }
}
