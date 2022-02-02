package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionDistanceNotValidException;
import nextstep.subway.exception.SectionNotFoundException;
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
        Section section1 = new Section(line, 강남역, 역삼역, 10);
        Section section2 = new Section(line, 역삼역, 선릉역, 10);
        Sections sections = new Sections();
        sections.add(section1);
        sections.add(section2);

        Section section = sections.getByDownStation(역삼역);
        assertThat(section).isEqualTo(section1);
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
    void validSectionDistance() {
        Section section1 = new Section(line, 강남역, 선릉역, 7);
        Section section2 = new Section(line, 역삼역, 선릉역, 7);
        Sections sections = new Sections();
        sections.add(section1);

        assertThatThrownBy(() -> {
            sections.add(section2);
        }).isInstanceOf(SectionDistanceNotValidException.class)
            .hasMessageMatching("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
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