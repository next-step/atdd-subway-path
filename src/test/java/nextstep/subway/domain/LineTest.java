package nextstep.subway.domain;

import nextstep.subway.domain.exception.InvalidLineColorException;
import nextstep.subway.domain.exception.InvalidLineNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    @Test
    @DisplayName("새로운 구간을 등록하면 신규 등록한 구간의 지하철역들을 찾을 수 있다.")
    void addSection() {
        //given
        Line line = new Line("2호선", "bg-green-600");
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        final int distance = 10;

        // when
        line.addSection(upStation, downStation, distance);

        // then
        assertThat(line.getStations()).containsOnly(upStation, downStation);
    }

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
    void Null_이거나_공란_인경우_이름_변경시_예외_반환(String name) {
        // given
        Line line = new Line("2호선", "bg-green-600");

        // when
        assertThatThrownBy(() -> line.changeName(name))
                .isInstanceOf(InvalidLineNameException.class);
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
    void Null_이거나_공란_인경우_색상_변경시_예외_반환(String name) {
        // given
        Line line = new Line("2호선", "bg-green-600");

        // when
        assertThatThrownBy(() -> line.changeColor(name))
                .isInstanceOf(InvalidLineColorException.class);
    }
}
