package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.SectionDistanceNotValidException;
import nextstep.subway.exception.SectionNotFoundException;
import nextstep.subway.exception.SectionValidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    private static final Line line = new Line("2호선", "bg-green-600");
    private static final Station 강남역 = new Station("강남역");
    private static final Station 역삼역 = new Station("역삼역");
    private static final Station 선릉역 = new Station("선릉역");

    @Test
    @DisplayName("마지막 하행역 조회")
    void getLastDownStation() {
        Section section1 = new Section(line, 강남역, 역삼역, 10);
        Section section2 = new Section(line, 역삼역, 선릉역, 10);
        Sections sections = new Sections();
        sections.add(section1);
        sections.add(section2);

        Station lastDownStation = sections.getLastDownStation();

        assertThat(lastDownStation).isEqualTo(선릉역);
    }

    @Test
    @DisplayName("구간의 모든 역 조회")
    void getAllStations() {
        Section section1 = new Section(line, 강남역, 역삼역, 7);
        Sections sections = new Sections();
        sections.add(section1);
        Section section2 = new Section(line, 선릉역, 강남역, 4);
        sections.add(section2);

        List<Station> stations = sections.getAllStations();

        assertThat(stations).hasSize(3);
    }

    @Test
    @DisplayName("하행역에 해당하는 구간 조회")
    void getByDownStation() {
        Section 강남역삼구간 = new Section(line, 강남역, 역삼역, 10);
        Section 역삼선릉구간 = new Section(line, 역삼역, 선릉역, 10);
        Sections sections = new Sections();
        sections.add(강남역삼구간);
        sections.add(역삼선릉구간);

        Section section = sections.getByDownStation(역삼역);
        assertThat(section).isEqualTo(강남역삼구간);
    }

    @Test
    @DisplayName("기존 구간의 역을 기준으로 새로운 구간을 추가 (하행역이 사이에 들어가는 경우)")
    void addBetweenDownStationSection() {
        Section section1 = new Section(line, 강남역, 역삼역, 7);
        Section section2 = new Section(line, 강남역, 선릉역, 4);
        Sections sections = new Sections();
        sections.add(section1);

        sections.add(section2);

        List<Section> allSections = sections.getAllSections();
        Section firstSection = getFirstSection(allSections, 선릉역);
        Section secondSection = getSecondSection(allSections, firstSection);

        assertThat(firstSection.getDistance()).isEqualTo(3);
        assertThat(firstSection.getUpStation()).isEqualTo(선릉역);
        assertThat(firstSection.getDownStation()).isEqualTo(역삼역);
        assertThat(secondSection.getDistance()).isEqualTo(4);
        assertThat(secondSection.getUpStation()).isEqualTo(강남역);
        assertThat(secondSection.getDownStation()).isEqualTo(선릉역);
    }

    @Test
    @DisplayName("기존 구간의 역을 기준으로 새로운 구간을 추가 (상행역이 사이에 들어가는 경우)")
    void addBetweenUpStationSection() {
        Section section1 = new Section(line, 강남역, 선릉역, 7);
        Section section2 = new Section(line, 역삼역, 선릉역, 4);
        Sections sections = new Sections();
        sections.add(section1);
        sections.add(section2);

        List<Section> allSections = sections.getAllSections();
        Section firstSection = getFirstSection(allSections, 강남역);
        Section secondSection = getSecondSection(allSections, firstSection);

        assertThat(firstSection.getDistance()).isEqualTo(3);
        assertThat(firstSection.getUpStation()).isEqualTo(강남역);
        assertThat(firstSection.getDownStation()).isEqualTo(역삼역);
        assertThat(secondSection.getDistance()).isEqualTo(4);
        assertThat(secondSection.getUpStation()).isEqualTo(역삼역);
        assertThat(secondSection.getDownStation()).isEqualTo(선릉역);
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    void addSectionUpStation() {
        Section section1 = new Section(line, 강남역, 역삼역, 7);
        Sections sections = new Sections();
        sections.add(section1);

        Section section2 = new Section(line, 선릉역, 강남역, 4);
        sections.add(section2);

        List<Section> allSections = sections.getAllSections();
        Section firstSection = getFirstSection(allSections, 선릉역);
        Section secondSection = getSecondSection(allSections, firstSection);


        assertThat(firstSection.getDistance()).isEqualTo(4);
        assertThat(firstSection.getUpStation()).isEqualTo(선릉역);
        assertThat(firstSection.getDownStation()).isEqualTo(강남역);
        assertThat(secondSection.getDistance()).isEqualTo(7);
        assertThat(secondSection.getUpStation()).isEqualTo(강남역);
        assertThat(secondSection.getDownStation()).isEqualTo(역삼역);
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    void addSectionDownStation() {
        Section section1 = new Section(line, 강남역, 역삼역, 7);
        Sections sections = new Sections();
        sections.add(section1);

        Section section2 = new Section(line, 역삼역, 선릉역, 4);
        sections.add(section2);

        List<Section> allSections = sections.getAllSections();
        Section firstSection = getFirstSection(allSections, 강남역);
        Section secondSection = getSecondSection(allSections, firstSection);

        assertThat(firstSection.getDistance()).isEqualTo(7);
        assertThat(firstSection.getUpStation()).isEqualTo(강남역);
        assertThat(firstSection.getDownStation()).isEqualTo(역삼역);
        assertThat(secondSection.getDistance()).isEqualTo(4);
        assertThat(secondSection.getUpStation()).isEqualTo(역삼역);
        assertThat(secondSection.getDownStation()).isEqualTo(선릉역);
    }

    @Test
    @DisplayName("기존 역 사이 길이보다 크거나 같으면 에러가 발생한다")
    void validateSectionDistance() {
        Section section1 = new Section(line, 강남역, 선릉역, 7);
        Section section2 = new Section(line, 역삼역, 선릉역, 7);
        Sections sections = new Sections();
        sections.add(section1);

        assertThatThrownBy(() -> {
            sections.add(section2);
        }).isInstanceOf(SectionDistanceNotValidException.class)
            .hasMessageMatching("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
    }

    @Test
    @DisplayName("구간 등록시 같은 구간이 있다면 에러가 발생한다")
    void hasSameSection() {
        Section section1 = new Section(line, 강남역, 선릉역, 7);
        Section section2 = new Section(line, 강남역, 선릉역, 7);
        Sections sections = new Sections();
        sections.add(section1);

        assertThatThrownBy(() -> {
            sections.add(section2);
        }).isInstanceOf(DuplicateSectionException.class);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나라도 포함되어 있지 않으면 에러가 발생한다")
    void validateSection() {
        Station 신도림역 = new Station("신도림역");
        Section section1 = new Section(line, 강남역, 역삼역, 7);
        Section section2 = new Section(line, 신도림역, 선릉역, 5);
        Sections sections = new Sections();

        sections.add(section1);

        assertThatThrownBy(() -> {
            sections.add(section2);
        }).isInstanceOf(SectionValidException.class);
    }

    @Test
    @DisplayName("중간역을 제거하면 구간이 재배치 된다")
    void removeRearrangeSection() {
        Station 삼성역 = new Station("삼성역");

        Section 강남역삼구간 = new Section(line, 강남역, 역삼역, 7);
        Section 역삼선릉구간 = new Section(line, 역삼역, 선릉역, 4);
        Section 선릉삼성구간 = new Section(line, 선릉역, 삼성역, 4);

        Sections sections = new Sections();
        sections.add(강남역삼구간);
        sections.add(역삼선릉구간);
        sections.add(선릉삼성구간);

        sections.remove(역삼역);

        assertThat(sections.getAllSections()).hasSize(2);
        assertThat(sections.getAllStations()).hasSize(3);
        assertThat(sections.getAllStations()).containsExactly(강남역, 선릉역, 삼성역);
    }

    @Test
    @DisplayName("중간역을 제거하면 구간이 재배치 후 거리가 합쳐진다.")
    void rearrangeSectionDistance() {
        Section 강남역삼구간 = new Section(line, 강남역, 역삼역, 7);
        Section 역삼선릉구간 = new Section(line, 역삼역, 선릉역, 4);

        Sections sections = new Sections();
        sections.add(강남역삼구간);
        sections.add(역삼선릉구간);

        sections.remove(역삼역);

        Section updatedSection = getFirstSection(sections.getAllSections(), 강남역);
        assertThat(updatedSection.getDistance()).isEqualTo(11);
    }

    private Section getFirstSection(List<Section> allSections, Station station) {
        return allSections
            .stream()
            .filter(it -> it.getUpStation().equals(station))
            .findFirst()
            .orElseThrow(SectionNotFoundException::new);
    }

    private Section getSecondSection(List<Section> allSections, Section firstSection) {
        return allSections
            .stream()
            .filter(it -> !it.equals(firstSection))
            .findFirst()
            .orElseThrow(SectionNotFoundException::new);
    }
}