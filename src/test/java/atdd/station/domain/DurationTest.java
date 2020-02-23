package atdd.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.temporal.ChronoField;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DurationTest {

    @Test
    void create() throws Exception {
        final LocalTime localTime = LocalTime.of(11, 10);

        final Duration duration = new Duration(localTime);

        assertThat(duration.getTime()).isEqualTo(localTime);
    }

    @DisplayName("create - 초 단위부터는 제거된다.")
    @Test
    void createTruncateMinutes() {
        final LocalTime localTime = LocalTime.of(11, 10, 40, 6666);
        final LocalTime expectedTime = LocalTime.of(11, 10);

        final Duration duration = new Duration(localTime);

        assertThat(duration.getTime()).isEqualTo(expectedTime);
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

    @Test
    void getSecondOfDay() throws Exception {
        final LocalTime time = LocalTime.of(12, 3);
        final Duration duration = new Duration(time);

        assertThat(duration.getSecondOfDay()).isEqualTo(time.getLong(ChronoField.SECOND_OF_DAY));
    }

    @Test
    void add() throws Exception {
        final Duration expectedDuration = new Duration(LocalTime.of(13, 0));

        final Duration duration1 = new Duration(LocalTime.of(10, 50));
        final Duration duration2 = new Duration(LocalTime.of(2, 10));


        final Duration result = duration1.add(duration2);


        assertThat(result).isEqualTo(expectedDuration);
    }

    @DisplayName("add - 소요시간의 합은 23:59 까지만 가능하다")
    @Test
    void addOverTime() throws Exception {
        final Duration duration1 = new Duration(LocalTime.of(20, 50));
        final Duration duration2 = new Duration(LocalTime.of(3, 10));


        assertThatThrownBy(() -> duration1.add(duration2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("소요 시간의 합은 23:59 까지만 가능합니다.");
    }

}