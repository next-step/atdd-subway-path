package atdd.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeTableTest {

    @DisplayName("시작 시간은 필수")
    @Test
    void startTimeIsNotNull() throws Exception {
        final LocalTime startTime = null;

        assertThatThrownBy(() -> new TimeTable(startTime, LocalTime.MIDNIGHT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작 시간은 필수 입니다.");
    }

    @DisplayName("종료 시간은 필수")
    @Test
    void endTimeIsNotNull() throws Exception {
        final LocalTime endTime = null;

        assertThatThrownBy(() -> new TimeTable(LocalTime.MIN, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("종료 시간은 필수 입니다.");
    }

    @DisplayName("시작 시간은 종료 시간보다 클 수 없다.")
    @Test
    void startTimeIsNotAfterThanEndDateTime() throws Exception {
        final LocalTime startTime = LocalTime.of(11, 0);
        final LocalTime endTime = startTime.minusNanos(1);

        assertThatThrownBy(() -> new TimeTable(startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작 시간은 종료 시간 보다 클 수 없습니다.");
    }

    @DisplayName("포멧형식은 HH:mm 이다.")
    @Test
    void getFormattedTime() throws Exception {
        final LocalTime startTime = LocalTime.of(11, 20, 43, 999);
        final LocalTime endTime = LocalTime.of(13, 38, 34, 999);

        final String expectedStartTime = "11:20";
        final String expectedEndTime = "13:38";

        final TimeTable timeTable = new TimeTable(startTime, endTime);

        assertThat(timeTable.getFormattedStartTime()).isEqualTo(expectedStartTime);
        assertThat(timeTable.getFormattedEndTime()).isEqualTo(expectedEndTime);
    }

}