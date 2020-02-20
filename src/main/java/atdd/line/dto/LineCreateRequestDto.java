package atdd.line.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Objects;

public class LineCreateRequestDto {

    @NotBlank
    private String name;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    private int intervalTime;

    private LineCreateRequestDto() { }

    public static LineCreateRequestDto of(String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        LineCreateRequestDto requestDto = new LineCreateRequestDto();
        requestDto.name = name;
        requestDto.startTime = startTime;
        requestDto.endTime = endTime;
        requestDto.intervalTime = intervalTime;
        return requestDto;
    }

    public String getName() {
        return name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    @Override
    public String toString() {
        return "LineCreateRequestDto{" +
                "name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", intervalTime=" + intervalTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineCreateRequestDto)) return false;
        LineCreateRequestDto that = (LineCreateRequestDto) o;
        return intervalTime == that.intervalTime &&
                name.equals(that.name) &&
                startTime.equals(that.startTime) &&
                endTime.equals(that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startTime, endTime, intervalTime);
    }

}
