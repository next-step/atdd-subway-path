package atdd.line.dto;

import atdd.line.domain.TimeTable;
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
    private List<StationDto> stations;

    private LineResponseDto() { }

    public LineResponseDto(Long id, String name, TimeTable timeTable, int intervalTime, List<StationDto> stations) {
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

    public List<StationDto> getStations() {
        return stations;
    }


    public static class StationDto {
        private Long id;
        private String name;

        private StationDto() { }

        public static StationDto of(Long id, String name) {
            StationDto station = new StationDto();
            station.id = id;
            station.name = name;
            return station;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "StationDto{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

    }

}
