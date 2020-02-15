package atdd.line.domain;

import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Embeddable
public class TimeTable {

    private static final DateTimeFormatter TIME_TABLE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    private TimeTable() { }

    public TimeTable(LocalTime startTime, LocalTime endTime) {
        Assert.notNull(startTime, "시작 시간은 필수 입니다.");
        Assert.notNull(endTime, "종료 시간은 필수 입니다.");
        Assert.isTrue(!startTime.isAfter(endTime), "시작 시간은 종료 시간 보다 클 수 없습니다.");

        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getFormattedStartTime() {
        return TIME_TABLE_FORMAT.format(startTime);
    }

    public String getFormattedEndTime() {
        return TIME_TABLE_FORMAT.format(endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeTable)) return false;
        TimeTable timeTable = (TimeTable) o;
        return Objects.equals(startTime, timeTable.startTime) &&
                Objects.equals(endTime, timeTable.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

}
