package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    void addSection() {
        Station 강남 = new Station("강남");
        Station 양재 = new Station("양재");
        Line line = new Line("신분당선", "red");

         line.addSection(강남, 양재, 6);

        List<Section> sections = line.getSections();
        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).getUpStation()).isEqualTo(강남);
        assertThat(sections.get(0).getDownStation()).isEqualTo(양재);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
