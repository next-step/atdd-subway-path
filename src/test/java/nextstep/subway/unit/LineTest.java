package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    Station 기흥역;
    Station 신갈역;
    Station 정자역;
    Section section_기흥역_신갈역;
    Section section_신갈역_정자역;
    Line line;

    @BeforeEach
    void setUp() {
        기흥역 = new Station(1L, "기흥역");
        신갈역 = new Station(2L, "신갈역");
        정자역 = new Station(3L, "정자역");
        section_기흥역_신갈역 = Section.builder()
                                 .upStation(기흥역)
                                 .downStation(신갈역)
                                 .distance(10).build();
        section_신갈역_정자역 = Section.builder()
                                 .upStation(신갈역)
                                 .downStation(정자역)
                                 .distance(10).build();
        line = new Line(4L, "분당선", "yellow");
    }

    @Test
    void addSection() {
        // when
        line.addSection(section_기흥역_신갈역);

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
        assertThat(line.getSections().getStationNames()).containsExactlyInAnyOrder("기흥역", "신갈역");
    }

    @Test
    void getStations() {
        // given
        line.addSection(section_기흥역_신갈역);

        // when
        List<Station> stations = line.getSections().getStations();

        // then
        assertThat(stations).containsExactlyInAnyOrder(기흥역, 신갈역);
        assertThat(stations).hasSize(2);
    }

    @Test
    void removeSection() {
        // given
        line.addSection(section_기흥역_신갈역);
        line.addSection(section_신갈역_정자역);

        // when
        line.getSections().removeSection(정자역.getId());

        // then
        assertThat(line.getSections().isContain(section_신갈역_정자역)).isFalse();
    }
}
