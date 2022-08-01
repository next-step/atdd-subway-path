package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        Line line = new Line("1호선", "blue");

        Station 구로역 = new Station("구로역");
        Station 신도림역 = new Station("신도림역");

        line.addSection(new Section(line, 구로역, 신도림역, 10));

        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
