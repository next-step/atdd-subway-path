package nextstep.subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}