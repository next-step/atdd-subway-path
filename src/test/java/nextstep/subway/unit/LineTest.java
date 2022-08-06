package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    Line 일호선_생성() {
        Line line = new Line();

        Station 구로역 = new Station("구로역");
        Station 신도림역 = new Station("신도림역");

        line.addSection(new Section(line, 구로역, 신도림역, 10));

        return line;
    }

    @Test
    void addSection() {
        Line line = 일호선_생성();

        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Test
    void getStations() {
        Line line = 일호선_생성();

        List<Station> stations = line.getStations();

        assertThat(stations.stream().map(Station::getName)
                .collect(Collectors.toList()))
                .contains("구로역", "신도림역");
    }

    @Test
    void removeSection() {
        // given
        Line line = 일호선_생성();

        Station 신도림역 = new Station("신도림역");
        Station 영등포역 = new Station("영등포역");

        line.addSection(new Section(line, 신도림역, 영등포역, 15));

        // when
        line.removeSection();

        // then
        List<Station> stations = line.getStations();
        assertThat(stations.stream().map(Station::getName)
                .collect(Collectors.toList()))
                .contains("구로역", "신도림역");

    }
}
