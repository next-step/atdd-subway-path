package nextstep.subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    @DisplayName("같은 상행역을 갖는지 확인할 수 있다.")
    @Test
    void sameUpStation() {
        //given
        final var section = new Section(new Station("잠실역"), new Station("강변역"), 10);
        //when
        boolean result = section.hasSameUpStation(new Section(new Station("잠실역"), new Station("잠실나루역"), 3));
        //then
        assertThat(result).isTrue();
    }

    @DisplayName("새로 추가 될 구간과의 거리를 구할 수 있다.")
    @Test
    void betweenDistance() {
        //given
        final var section = new Section(new Station("잠실역"), new Station("강변역"), 10);
        //when
        int between = section.betweenDistance(new Section(new Station("잠실역"), new Station("잠실나루역"), 3));
        //then
        assertThat(between).isEqualTo(7);
    }

    @DisplayName("새로 추가 될 구간이 기존과 같거나 크다면 에러가 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void betweenDistanceException(int distance) {
        //given
        final var section = new Section(new Station("잠실역"), new Station("강변역"), 10);
        //when, then
        assertThatThrownBy(() -> section.betweenDistance(new Section(new Station("잠실역"), new Station("잠실나루역"), distance)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가 될 구간의 거리가 크거나 같을 수 없습니다.");
    }
}