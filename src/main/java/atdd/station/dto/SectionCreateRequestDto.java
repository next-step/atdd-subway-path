package atdd.station.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Objects;

public class SectionCreateRequestDto {

    @NotNull
    private Long nextStationId;
    @NotNull
    private LocalTime duration;
    @NotNull
    private double distance;

    private SectionCreateRequestDto() { }

    public static SectionCreateRequestDto of(Long nextStationId, LocalTime duration, double distance) {
        SectionCreateRequestDto requestDto = new SectionCreateRequestDto();
        requestDto.nextStationId = nextStationId;
        requestDto.duration = duration;
        requestDto.distance = distance;
        return requestDto;
    }

    public Long getNextStationId() {
        return nextStationId;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SectionCreateRequestDto)) return false;
        SectionCreateRequestDto that = (SectionCreateRequestDto) o;
        return Double.compare(that.distance, distance) == 0 &&
                Objects.equals(nextStationId, that.nextStationId) &&
                Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nextStationId, duration, distance);
    }

    @Override
    public String toString() {
        return "SectionCreateRequestDto{" +
                "nextStationId=" + nextStationId +
                ", duration=" + duration +
                ", distance=" + distance +
                '}';
    }

}
