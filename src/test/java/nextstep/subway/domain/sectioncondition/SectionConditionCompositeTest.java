package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.StationTestSources.station;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SectionConditionCompositeTest {

    private SectionCondition target;

    @BeforeEach
    void setUp() {
        target = new SectionConditionComposite(List.of(
                new EmptySectionsCondition(),
                new LastDownStationCondition(),
                new FirstUpStationCondition(),
                new BetweenOnUpStationCondition(),
                new BetweenOnDownStationCondition()));
    }

    @Test
    void addSection_Empty() {
        // given
        final Line line = new Line();
//        line.addSection(station(1), station(2), 10);

        // when
        target.add(line, new AddSectionRequest(station(2), station(3), 10));

        // then
        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void addSection_BetweenDownStation() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        line.addSection(new Section(line, station1, station3, 10));
        line.addSection(new Section(line, station3, station4, 10));

        // when
        target.add(line, new AddSectionRequest(station2, station3, 4));

        // then
        final List<Section> sections = line.getSections();
        assertSections(station1, station2, 6, sections.get(0));
        assertSections(station2, station3, 4, sections.get(1));
    }

    @Test
    void addSection_BetweenDownStationOverLength() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);


        line.addSection(new Section(line, station1, station3, 10));
        line.addSection(new Section(line, station3, station4, 10));

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.add(line, new AddSectionRequest(station2, station3, 100)));

        // then
        assertThat(result).isNotNull();
        assertThat(line.getSections()).hasSize(2);
    }

    @Test
    void addSection_BetweenUpStation() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        line.addSection(new Section(line, station1, station2, 10));
        line.addSection(new Section(line, station2, station4, 10));

        // when
        target.add(line, new AddSectionRequest(station2, station3, 4));

        // then
        final List<Section> sections = line.getSections();

        assertSections(station2, station3, 4, sections.get(1));
        assertSections(station3, station4, 6, sections.get(2));
    }

    @Test
    void addSection_BetweenUpStationOverLength() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        line.addSection(new Section(line, station1, station2, 10));
        line.addSection(new Section(line, station2, station4, 10));

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.add(line, new AddSectionRequest(station2, station3, 100)));

        // then
        assertThat(result).isNotNull();
        assertThat(line.getSections()).hasSize(2);
    }

    private void assertSections(final Station upStation, final Station downStation, final int distance, final Section section) {
        assertThat(section.getUpStation()).isEqualTo(upStation);
        assertThat(section.getDownStation()).isEqualTo(downStation);
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    @Test
    void addSection_FirstUpStation() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        line.addSection(new Section(line, station2, station3, 10));

        // when
        target.add(line, new AddSectionRequest(station1, station2, 10));

        // then
        assertThat(line.getStations()).containsExactly(station1, station2, station3);
    }

    @Test
    void addSection_LastDownStation() {
        // given
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        line.addSection(new Section(line, station1, station2, 10));

        // when
        target.add(line, new AddSectionRequest(station2, station3, 10));

        // then
        assertThat(line.getStations()).containsExactly(station1, station2, station3);
    }

}