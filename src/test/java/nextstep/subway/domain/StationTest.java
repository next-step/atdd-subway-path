package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @Test
    @DisplayName("id 값이 같은지 검증")
    void isEqualToId() {
        //given
        Station station = new Station(1L, "강남역");

        //when
        final boolean actual = station.isEqualToId(1L);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("id 값이 다른지 검증")
    void isNotEqualToId() {
        //given
        Station station = new Station(1L, "강남역");

        //when
        final boolean actual = station.isEqualToId(2L);

        //then
        assertThat(actual).isFalse();
    }

}