package nextstep.subway.domain;

import nextstep.subway.domain.exceptions.NotPositiveNumberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathTest {

    Station 강남역;
    Station 양재역;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        양재역 = new Station(2L, "양재역");
    }

    @Test
    void Path를_정상적으로_생성한다() {
        // given
        List<Station> stations = List.of(강남역, 양재역);
        int distance = 10;

        // when then
        assertThat(Path.create(stations, distance)).isNotNull();
    }


    @Test
    void Path의_역_개수가_1개_이하인_경우_예외가_발생한다() {
        // given
        List<Station> stations = List.of(강남역);
        int distance = 10;

        // when then
        assertAll(() -> {
            assertThatThrownBy(() -> Path.create(stations, distance)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> Path.create(List.of(), distance)).isInstanceOf(IllegalArgumentException.class);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void Path의_거리는_0보다_작거나_같을_수_없다(int distance) {
        // given
        List<Station> stations = List.of(강남역, 양재역);

        // when
        assertThatThrownBy(() -> Path.create(stations, distance)).isInstanceOf(NotPositiveNumberException.class);
    }
}
