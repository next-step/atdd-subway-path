package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SectionsTest {

    private Sections sections;

    private Line line;
    private Station station1;
    private Station station2;


    @BeforeEach
    void setUp() {
        sections = new Sections();

        line = new Line("4호선", "blue");
        station1 = new Station("중앙역");
        station2 = new Station("한대앞역");
    }

    @Test
    @DisplayName("구간이 정상적으로 추가된다.")
    void addTest() {
        addSectionMockSection();
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("구간이 정상적으로 조회된다.")
    void getStationsTest() {
        assertThat(sections.getStations()).isEmpty();
        addSectionMockSection();
        assertThat(sections.getStations()).containsAnyOf(station1, station2);
    }

    @Test
    @DisplayName("구간의 마지막 역이 조회된다.")
    void lastStationTest() {
        addSectionMockSection();
        assertThat(sections.lastStation()).isEqualTo(station2);
    }

    @Test
    @DisplayName("구간의 마지막 역이 삭제된다.")
    void deleteLastStationTest() {
        Station station3 = new Station("상록수역");
        addSectionMockSection();
        sections.add(new Section(line, station2, station3, 10));

        sections.deleteLastStation();

        assertThat(sections.lastStation()).isEqualTo(station2);
    }

    @Test
    @DisplayName("구간의 마지막 Index가 조회된다.")
    void sectionsLastIndexTest() {
        assertThat(sections.sectionsLastIndex()).isEqualTo(-1);
        addSectionMockSection();

        assertThat(sections.sectionsLastIndex()).isEqualTo(0);
    }

    private void addSectionMockSection() {
        sections.add(new Section(line, station1, station2, 10));
    }
}