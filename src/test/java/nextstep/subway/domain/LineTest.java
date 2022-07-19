package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    @DisplayName("노선의 이름을 변경하면 이름이 변경된다.")
    void changeName() {
        // given
        Line line = new Line("2호선", "bg-green-600");

        // when
        line.changeName("신분당선");

        // then
        assertThat(line.getName()).isEqualTo("신분당선");
    }

    @ParameterizedTest(name = "#{index} - test args={0}")
    @ValueSource(strings = {"", " "})
    @NullSource
    void Null_이거나_공란_인경우_이름_변경시_스킵한다(String name) {
        // given
        Line line = new Line("2호선", "bg-green-600");

        // when
        line.changeName(name);

        // then
        assertThat(line.getName()).isEqualTo("2호선");
    }

    @Test
    @DisplayName("노선의 색상을 변경하면 색상이 변경된다.")
    void changeColor() {
        // given
        Line line = new Line("2호선", "bg-green-600");

        // when
        line.changeColor("bg-red-600");

        // then
        assertThat(line.getColor()).isEqualTo("bg-red-600");
    }

    @ParameterizedTest(name = "#{index} - test args={0}")
    @ValueSource(strings = {"", " "})
    @NullSource
    void Null_이거나_공란_인경우_색상_변경시_스킵한다(String color) {
        // given
        Line line = new Line("2호선", "bg-green-600");

        // when
        line.changeColor(color);

        // then
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }
}
