package nextstep.subway.domain;

import static nextstep.subway.unit.StationUnitSteps.역_추가;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import nextstep.subway.unit.StationUnitSteps;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    Station station;

    @BeforeEach
    void setUp() {
        station = 역_추가(1L, "강남역");
    }

    @Test
    @DisplayName("id 값이 같은지 검증")
    void isEqualToId() {
        //when
        final boolean actual = station.isEqualToId(1L);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("id 값이 다른지 검증")
    void isNotEqualToId() {
        //when
        final boolean actual = station.isEqualToId(2L);

        //then
        assertThat(actual).isFalse();
    }

}