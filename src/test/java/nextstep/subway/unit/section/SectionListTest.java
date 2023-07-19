package nextstep.subway.unit.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionList;
import nextstep.subway.section.domain.SectionStations;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

class SectionListTest {

    private final Station a = new Station("a");
    private final Station b = new Station("b");
    private final Station c = new Station("c");
    private final Station d = new Station("d");

    private final Section section = new Section(new SectionStations(a, b), 5);
    private final Line line = new Line("line", "bg",  section);

    @Test
    void addSectionInterStationWhenHasSameUpwardStation() {
        SectionList sectionList = line.getSections();
        Section newSection = new Section(line, new SectionStations(a, c), 4);

        sectionList.addSection(newSection);

        List<Section> addedSection = sectionList.getSections();
        assertThat(addedSection).hasSize(2);

        Section sectionAC = addedSection.get(0);
        assertThat(sectionAC.getUpwardStation()).isEqualTo(a);
        assertThat(sectionAC.getDownwardStation()).isEqualTo(c);
        assertThat(sectionAC.getDistance()).isEqualTo(4);

        Section sectionBC = addedSection.get(1);
        assertThat(sectionBC.getUpwardStation()).isEqualTo(c);
        assertThat(sectionBC.getDownwardStation()).isEqualTo(b);
        assertThat(sectionBC.getDistance()).isEqualTo(1);
    }

    @Test
    void addSectionInterStationWhenHasSameDownwardStation() {
        SectionList sectionList = line.getSections();
        Section newSection = new Section(line, new SectionStations(d, b), 4);

        sectionList.addSection(newSection);

        List<Section> addedSection = sectionList.getSections();
        assertThat(addedSection).hasSize(2);

        Section sectionAD = addedSection.get(0);
        assertThat(sectionAD.getUpwardStation()).isEqualTo(a);
        assertThat(sectionAD.getDownwardStation()).isEqualTo(d);
        assertThat(sectionAD.getDistance()).isEqualTo(1);

        Section sectionDB = addedSection.get(1);
        assertThat(sectionDB.getUpwardStation()).isEqualTo(d);
        assertThat(sectionDB.getDownwardStation()).isEqualTo(b);
        assertThat(sectionDB.getDistance()).isEqualTo(4);
    }

    @Test
    void addSectionWhenLastDownwardStation() {
        SectionList sectionList = line.getSections();
        Section newSection = new Section(line, new SectionStations(b, c), 4);

        sectionList.addSection(newSection);

        List<Section> addedSection = sectionList.getSections();
        assertThat(addedSection).hasSize(2);
        assertThat(addedSection.get(0).getDistance()).isEqualTo(5);
        assertThat(addedSection.get(1).getDistance()).isEqualTo(4);
    }

    @Test
    void addSectionWhenLastUpwardStation() {
        SectionList sectionList = line.getSections();
        Section newSection = new Section(line, new SectionStations(c, a), 4);

        sectionList.addSection(newSection);

        List<Section> addedSection = sectionList.getSections();
        assertThat(addedSection).hasSize(2);
        assertThat(addedSection.get(0).getDistance()).isEqualTo(5);
        assertThat(addedSection.get(1).getDistance()).isEqualTo(4);
    }

    @Test
    void addSectionExceptionWhenNotFoundAllStation() {
        SectionList sectionList = line.getSections();
        Section newSection = new Section(line, new SectionStations(c, d), 4);

        assertThrows(CustomException.class, () -> sectionList.addSection(newSection));
    }

    @Test
    void addSectionExceptionWhenAllStationIncluded() {
        SectionList sectionList = line.getSections();
        Section newSection = new Section(line, new SectionStations(b, c), 4);
        sectionList.addSection(newSection);

        Section allIncludeSection = new Section(line, new SectionStations(a, c), 4);

        assertThrows(CustomException.class, () -> sectionList.addSection(allIncludeSection));
    }

    @Test
    void getDownLastStation() {
        SectionList sectionList = line.getSections();

        Station lastDownStation = sectionList.getDownLastStation();

        assertThat(lastDownStation).isEqualTo(b);
    }

    @Test
    void getDownLastStationWhenAddSection() {
        SectionList sectionList = line.getSections();
        Section newSection = new Section(line, new SectionStations(b, c), 4);
        sectionList.addSection(newSection);

        Station lastDownStation = sectionList.getDownLastStation();

        assertThat(lastDownStation).isEqualTo(c);
    }

    @Test
    void getUpLastStation() {
        SectionList sectionList = line.getSections();

        Station lastUpStation = sectionList.getUpLastStation();

        assertThat(lastUpStation).isEqualTo(a);
    }

    @Test
    void getUpLastStationWhenAddUpwardStation() {
        SectionList sectionList = line.getSections();
        Section newSection = new Section(line, new SectionStations(c, a), 4);
        sectionList.addSection(newSection);

        Station lastUpStation = sectionList.getUpLastStation();

        assertThat(lastUpStation).isEqualTo(c);
    }
}
