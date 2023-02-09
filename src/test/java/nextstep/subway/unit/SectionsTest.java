package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    private Line 이호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections();
        이호선 = new Line(1L, "2호선", "bg-green-color");
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(1L, "역삼역");
        선릉역 = new Station(1L, "선릉역");
    }

    @Test
    void addSectionBetweenStations() {
        // when
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 강남역, 선릉역, 9);

        // then
        assertThat(sections.getStations()).containsExactly(강남역, 선릉역, 역삼역);
        assertThat(sections.getSections().stream()
                .filter(section -> section.getUpStation().equals(강남역))
                .filter(section -> section.getDownStation().equals(선릉역))
                .findFirst().get()
                .getDistance()).isEqualTo(9);
        assertThat(sections.getSections().stream()
                .filter(section -> section.getUpStation().equals(선릉역))
                .filter(section -> section.getDownStation().equals(역삼역))
                .findFirst().get()
                .getDistance()).isEqualTo(1);
    }

    @Test
    void addSectionWithLastUpStation() {
        // when
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 선릉역, 강남역, 10);

        // then
        assertThat(sections.getStations()).containsExactly(선릉역, 강남역, 역삼역);
    }

    @Test
    void addSectionWithLastDownStation() {
        // when
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 선릉역, 강남역, 10);

        // then
        assertThat(sections.getStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @Test
    void cannotAddSectionBetweenStationsWithInvalidDistance() {
        // given
        sections.add(이호선, 강남역, 역삼역, 10);

        // when, then
        assertThatThrownBy(() -> {
            sections.add(이호선, 강남역, 선릉역, 10);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            sections.add(이호선, 강남역, 선릉역, 11);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cannotAddSectionUpStationAndDownStationIsAlreadyExists() {
        // given
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 역삼역, 선릉역, 10);

        // when, then
        assertThatThrownBy(() -> {
            sections.add(이호선, 강남역, 선릉역, 3);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cannotAddSectionUpStationAndDownStationDoesNotContainInLine() {
        // given
        final Station 당산역 = new Station("당산역");
        final Station 선유도역 = new Station("선유도역");
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 역삼역, 선릉역, 10);

        // when, then
        assertThatThrownBy(() -> {
            sections.add(이호선, 당산역, 선유도역, 7);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getStations() {
        // given
        sections.add(이호선, 강남역, 역삼역, 10);

        // when
        final List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsAnyOf(강남역, 역삼역);
    }

    @Test
    void removeSection() {
        // given
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 역삼역, 선릉역, 10);

        // when
        sections.remove(선릉역);

        // then
        final List<Station> stations = sections.getStations();
        assertThat(stations).containsOnly(강남역, 역삼역);
    }
}
