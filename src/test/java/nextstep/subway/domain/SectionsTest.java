package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SectionsTest {

    Sections sections;
    Line line = new Line("신분당선", "green");
    Station up = new Station("강남역");
    Station down = new Station("양재역");
    int distance = 10;

    Section section = new Section(up, down, distance);

    @BeforeEach
    void setUp() {
        sections = new Sections();
        sections.addSection(section);
    }

    @Test
    void addSection() {
        Station newDown = new Station("판교");
        Section section = new Section(up, newDown, 5);
        sections.addSection(section);

        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(up, newDown, down);
    }
}