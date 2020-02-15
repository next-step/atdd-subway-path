package atdd.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationTest {

    @Test
    void create()  {
        final String name = "name!!";

        Station station = Station.create(name);

        assertThat(station.getName()).isEqualTo(name);
    }

    @DisplayName("name 은 빈값이나 null 일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createWithNullAndEmptyName(String name) {
        assertThatThrownBy(() -> Station.create(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name은 필수 입니다.");
    }

}