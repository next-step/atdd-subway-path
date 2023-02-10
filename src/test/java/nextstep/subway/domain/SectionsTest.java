package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class SectionsTest {
    @Test
    void addSection() {
        // given
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        Line line = new Line("2호선", "green");
        Section section = new Section(line, upStation, downStation, 10);
        Sections sections = new Sections();

        // when
        sections.addSection(line, section);

        // then
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    void getFirstSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");
        var distance = 10;
        Line line = new Line("2호선", "green");
        Section firstSection = new Section(line, 강남역, 교대역, distance);
        Section secondSection = new Section(line, 교대역, 서초역, distance);
        Sections sections = new Sections();
        sections.addSection(line, firstSection);
        sections.addSection(line, secondSection);

        // when
        Section section = sections.getFirstSection();

        // then
        assertThat(section.getUpStation()).isEqualTo(firstSection.getUpStation());
        assertThat(section.getDownStation()).isEqualTo(firstSection.getDownStation());
        assertThat(section.getDistance()).isEqualTo(firstSection.getDistance());
    }

    @Test
    void getLastSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");
        var distance = 10;
        Line line = new Line("2호선", "green");
        Section firstSection = new Section(line, 강남역, 교대역, distance);
        Section secondSection = new Section(line, 교대역, 서초역, distance);
        Sections sections = new Sections();
        sections.addSection(line, firstSection);
        sections.addSection(line, secondSection);

        // when
        Section section = sections.getLastSection();

        // then
        assertThat(section.getUpStation()).isEqualTo(secondSection.getUpStation());
        assertThat(section.getDownStation()).isEqualTo(secondSection.getDownStation());
        assertThat(section.getDistance()).isEqualTo(secondSection.getDistance());
    }

    @Test
    void getSectionByUpStation() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");
        var distance = 10;
        Line line = new Line("2호선", "green");
        Section firstSection = new Section(line, 강남역, 교대역, distance);
        Section secondSection = new Section(line, 교대역, 서초역, distance);
        Sections sections = new Sections();
        sections.addSection(line, firstSection);
        sections.addSection(line, secondSection);

        // when
        Section section = sections.getSectionByUpStation(강남역);

        // then
        assertThat(section.getUpStation()).isEqualTo(firstSection.getUpStation());
        assertThat(section.getDownStation()).isEqualTo(firstSection.getDownStation());
        assertThat(section.getDistance()).isEqualTo(firstSection.getDistance());
    }

    @DisplayName("구간을 제거한다. (첫번째 구간 제거)")
    @Test
    void removeFirstSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");
        var distance = 10;
        Line line = new Line("2호선", "green");
        Section firstSection = new Section(line, 강남역, 교대역, distance);
        Section secondSection = new Section(line, 교대역, 서초역, distance);
        Sections sections = new Sections();
        sections.addSection(line, firstSection);
        sections.addSection(line, secondSection);

        // when
        sections.removeSection(강남역);

        // then
        List<Section> foundSections = sections.getSections();
        assertThat(foundSections.size()).isEqualTo(1);
        assertThat(foundSections.get(0).getUpStation()).isEqualTo(교대역);
        assertThat(foundSections.get(0).getDownStation()).isEqualTo(서초역);
        assertThat(foundSections.get(0).getDistance()).isEqualTo(distance);
    }

    @DisplayName("구간을 제거한다. (마지막 구간 제거)")
    @Test
    void removeLastSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");
        var distance = 10;
        Line line = new Line("2호선", "green");
        Section firstSection = new Section(line, 강남역, 교대역, distance);
        Section secondSection = new Section(line, 교대역, 서초역, distance);
        Sections sections = new Sections();
        sections.addSection(line, firstSection);
        sections.addSection(line, secondSection);

        // when
        sections.removeSection(서초역);

        // then
        List<Section> foundSections = sections.getSections();
        assertThat(foundSections.size()).isEqualTo(1);
        assertThat(foundSections.get(0).getUpStation()).isEqualTo(강남역);
        assertThat(foundSections.get(0).getDownStation()).isEqualTo(교대역);
        assertThat(foundSections.get(0).getDistance()).isEqualTo(distance);
    }

    @DisplayName("구간을 제거한다. (중간 구간 제거)")
    @Test
    void removeMiddleSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");
        var distance = 10;
        Line line = new Line("2호선", "green");
        Section firstSection = new Section(line, 강남역, 교대역, distance);
        Section secondSection = new Section(line, 교대역, 서초역, distance);
        Sections sections = new Sections();
        sections.addSection(line, firstSection);
        sections.addSection(line, secondSection);

        // when
        sections.removeSection(교대역);

        // then
        List<Section> foundSections = sections.getSections();
        assertThat(foundSections.size()).isEqualTo(1);
        assertThat(foundSections.get(0).getUpStation()).isEqualTo(강남역);
        assertThat(foundSections.get(0).getDownStation()).isEqualTo(서초역);
        assertThat(foundSections.get(0).getDistance()).isEqualTo(firstSection.getDistance() + secondSection.getDistance());
    }

    @DisplayName("노선에 등록되어있지 않은 구간 삭제 시 예외를 던진다.")
    @Test
    void removeSectionNotRegisteredInLine() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        var distance = 10;
        Line line = new Line("2호선", "green");
        Section section = new Section(line, 강남역, 교대역, distance);
        Sections sections = new Sections();
        sections.addSection(line, section);

        // when - then
        Station 서초역 = new Station("서초역");
        assertThrowsExactly(DeletedStationNotRegisteredInLineException.class, () -> sections.removeSection(서초역));
    }
}
