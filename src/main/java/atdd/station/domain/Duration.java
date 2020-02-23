package atdd.station.domain;

import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Embeddable
public class Duration {

    @Column(name = "duration", nullable = false)
    private LocalTime time;

    private Duration() { }

    public Duration(LocalTime time) {
        Assert.notNull(time, "시간은 필수값 입니다.");
        Assert.isTrue(time.isAfter(LocalTime.MIN), "시간은 00:00 보다 커야 합니다.");
        this.time = time.truncatedTo(ChronoUnit.MINUTES);
    }

    public LocalTime getTime() {
        return time;
    }

    public long getSecondOfDay() {
        return time.getLong(ChronoField.SECOND_OF_DAY);
    }

    public Duration add(Duration other) {
        final long totalSecondsOfDay = sumSecondsOfDay(other);
        checkValidTimeRange(totalSecondsOfDay);
        return new Duration(LocalTime.ofSecondOfDay(totalSecondsOfDay));
    }

    private long sumSecondsOfDay(Duration other) {
        final long secondsOfDay = getSecondOfDay();
        final long otherSecondsOfDay = other.getSecondOfDay();
        return secondsOfDay + otherSecondsOfDay;
    }

    private void checkValidTimeRange(long totalSecondsOfDay) {
        try {
            ChronoField.SECOND_OF_DAY.checkValidValue(totalSecondsOfDay);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("소요 시간의 합은 23:59 까지만 가능합니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Duration)) return false;
        Duration duration = (Duration) o;
        return Objects.equals(time, duration.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }

}
