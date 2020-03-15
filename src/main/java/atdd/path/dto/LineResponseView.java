package atdd.path.dto;

import atdd.path.domain.Station;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class LineResponseView {
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private List<Station> stations = new ArrayList<>();

    public LineResponseView() {
    }

    @Builder
    public LineResponseView(Long id, String name, LocalTime startTime, LocalTime endTime,
                            int intervalTime, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.stations = stations;
    }
}
