package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LineTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line("4호선", "blue");
    }

    @Test
    @DisplayName("구간이 정상적으로 추가된다.")
    void addSectionTest() {
        assertThat(line.getSections()).isEmpty();

        line.addSection(new Section(line, new Station("중앙역"), new Station("한대앞역"), 10));
        assertThat(line.getSections()).hasSize(1);
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
}