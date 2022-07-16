package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LineTest {

    @Test
    @DisplayName("Line이 정상적으로 생성된다.")
    void createLineTest() {
        assertDoesNotThrow(() -> new Line("4호선", "blue"));
    }

    @Test
    @DisplayName("구간이 정상적으로 추가된다.")
    void addSectionTest() {
        Line line = new Line("4호선", "blue");
        assertThat(line.getSections()).isEmpty();

        line.addSection(new Section(line, new Station("중앙역"), new Station("한대앞역"), 10));
        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    @DisplayName("name과 color가 정상적으로 변경된다.")
    void updateNameAndColorTest() {
        Line line = new Line("4호선", "blue");
        line.updateNameAndColor("2호선", "green");

        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("green");
    }

    @Test
    @DisplayName("name과 color가 null 이면 에러 없이 변경되지 않는다.")
    void updateNameAndColorNullTest() {
        Line line = new Line("4호선", "blue");
        line.updateNameAndColor(null, null);

        assertThat(line.getName()).isEqualTo("4호선");
        assertThat(line.getColor()).isEqualTo("blue");
    }
}