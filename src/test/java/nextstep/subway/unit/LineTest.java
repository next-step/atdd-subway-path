package nextstep.subway.unit;

import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LineTest {

    private Station upStation;
    private Station downStation;
    private Station newStation;
    private Line line;
    private Section section;
    private Section newSection;

    @BeforeEach
    void setUp() {
        upStation = new Station("상행역");
        downStation = new Station("하행역");
        newStation = new Station("새로운 하행역");
        line = new Line("2호선", "green", 20L);
        section = new Section(line, upStation, downStation, 10L);
        newSection = new Section(line, downStation, newStation, 10L);
    }

    @Test
    void addSection() {
        // when
        line.addSection(section);

        // then
        List<Section> sections = line.getSections();
        Assertions.assertThat(sections).hasSize(1);
    }

    @Test
    void getStations() {
        // when
        List<Section> sections = line.getSections();

        // then
        Assertions.assertThat(sections).isNotNull();
    }

    @Test
    void removeSection() {
        // given
        line.addSection(section);
        line.addSection(newSection);
        List<Section> sections = line.getSections();
        System.out.println("sections.size() = " + sections.size());

        // when
        line.removeSection(newStation);

        // then
        Assertions.assertThat(sections).hasSize(1);

    }
}
