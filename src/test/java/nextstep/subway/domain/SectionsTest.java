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

        sections.add(new Section(line, station1, station2, 10));
    }

    @Test
    @DisplayName("구간이 정상적으로 추가된다.")
    void addTest() {
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("구간이 정상적으로 조회된다.")
    void getStationsTest() {
        assertThat(sections.getStations()).containsAnyOf(station1, station2);
    }

    @Test
    @DisplayName("제거할 역이 마지막 역인 경우 제거된다.")
    void deleteStationTest() {
        Station station3 = new Station("상록수역");
        sections.add(new Section(line, station2, station3, 10));

        assertThat(sections.size()).isEqualTo(2);

        sections.deleteStation(station3);
        assertThat(sections.getStations()).hasSize(2);
        assertThat(sections.getStations()).doesNotContain(station3);
    }

    @Test
    @DisplayName("제거할 역이 마지막 역이 아닌경우 에러가 발생한다.")
    void deleteStationFailTest() {
        assertThatIllegalArgumentException().isThrownBy(() -> sections.deleteStation(station1));
        assertThatIllegalArgumentException().isThrownBy(() -> sections.deleteStation(new Station("없는역")));
    }
}