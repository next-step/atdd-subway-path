package atdd.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DurationTest {

    @Test
    void create() throws Exception {
        final LocalTime localTime = LocalTime.of(11, 10);

        final Duration duration = new Duration(localTime);

        assertThat(duration.getDuration()).isEqualTo(localTime);
    }

    @DisplayName("시간은 null 일 수 없다.")
    @Test
    void createNullValue() throws Exception {
        assertThatThrownBy(() -> new Duration(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시간은 필수값 입니다.");
    }

    @DisplayName("시간은 00:00 일 수 없다.")
    @Test
    void createMinValue() throws Exception {
        final LocalTime localTime = LocalTime.MIN;

        assertThatThrownBy(() -> new Duration(localTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시간은 00:00 보다 커야 합니다.");
    }

}