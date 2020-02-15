package atdd.line.dto;

import atdd.line.domain.TimeTable;
import atdd.station.dto.StationResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;
import java.util.List;

public class LineResponseDto {

    private Long id;
    private String name;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private int intervalTime;
    private List<StationResponseDto> stations;

    private LineResponseDto() { }

    public LineResponseDto(Long id, String name, TimeTable timeTable, int intervalTime, List<StationResponseDto> stations) {
        this.id = id;
        this.name = name;
        this.startTime = timeTable.getStartTime();
        this.endTime = timeTable.getEndTime();
        this.intervalTime = intervalTime;
        this.stations = stations;
    }

    public Long getId() {
        return id;
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

    public List<StationResponseDto> getStations() {
        return stations;
    }

}
