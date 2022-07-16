package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class LineTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line("4호선", "blue");
    }

    @Test
    @DisplayName("구간이 정상적으로 추가된다.")
    void addSectionTest() {
        assertThat(line.getSections().size()).isEqualTo(0);

        line.addSection(new Section(line, new Station("중앙역"), new Station("한대앞역"), 10));
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("name과 color가 정상적으로 변경된다.")
    void updateNameAndColorTest() {
        line.updateNameAndColor("2호선", "green");

        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("green");
    }

    @Test
    @DisplayName("name과 color가 null 이면 에러 없이 변경되지 않는다.")
    void updateNameAndColorNullTest() {
        line.updateNameAndColor(null, null);

        assertThat(line.getName()).isEqualTo("4호선");
        assertThat(line.getColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("Line이 가지고 있는 역 목록이 반환된다.")
    void getStationsTest() {
        assertThat(line.getStations()).isEmpty();

        Station station1 = new Station("중앙역");
        Station station2 = new Station("한대앞역");

        line.addSection(new Section(line, station1, station2, 10));
        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getStations()).containsAnyOf(station1, station2);

    }

    @Test
    @DisplayName("제거할 역이 Line의 마지막 역인 경우 제거된다.")
    void deleteStationTest() {
        Station station1 = new Station("중앙역");
        Station station2 = new Station("한대앞역");
        Station station3 = new Station("상록수역");

        line.addSection(new Section(line, station1, station2, 10));
        line.addSection(new Section(line, station2, station3, 10));
        assertThat(line.getStations()).hasSize(3);

        line.deleteStation(station3);
        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getStations()).doesNotContain(station3);
    }

    @Test
    @DisplayName("제거할 역이 Line의 마지막 역이 아닌경우 에러가 발생한다.")
    void deleteStationFailTest() {

        Station station1 = new Station("중앙역");
        Station station2 = new Station("한대앞역");

        line.addSection(new Section(line, station1, station2, 10));

        assertThatIllegalArgumentException().isThrownBy(() -> line.deleteStation(station1));
        assertThatIllegalArgumentException().isThrownBy(() -> line.deleteStation(new Station("없는역")));
    }
}