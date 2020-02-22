package atdd.station.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
public class LineDto {
    private long id;
    private String name;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private int intervalTime;

    private List<IdNameDto> stations;

    public LineDto() {
    }

    @Builder
    private LineDto(long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, List<IdNameDto> stations) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.stations = stations;
    }
}
