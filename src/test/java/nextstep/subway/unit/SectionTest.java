package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SectionTest {

    @DisplayName("Section을 정상적으로 생성한다")
    @Test
    public void create_section_test() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("건대입구역");

        // when
        Section section = new Section(line, upStation, downStation, 10);

        // then
        assertAll(
                () -> assertThat(section.getUpStation()).isEqualTo(upStation),
                () -> assertThat(section.getDownStation()).isEqualTo(downStation)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void create_section_with_invalid_distance(int distance) {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("건대입구역");

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Section(line, upStation, downStation, distance));

        // then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간이 가지고 있는 하행종점역이 아닐경우 true를 반환한다")
    @Test
    public void dont_have_down_station() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station(1L, "강남역");
        Station downStation = new Station(2L, "건대입구역");
        Section section = new Section(line, upStation, downStation, 10);

        // when
        boolean result = section.hasNotDownStation(upStation);

        // then
        assertThat(result).isTrue();
    }
}
