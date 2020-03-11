package atdd.path.application.dto;

import atdd.path.domain.Edge;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
public class LineResponseView {
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int interval;
    private List<Station> stations;
    private List<Edge> edges;

    public LineResponseView() {
    }

    @Builder
    public LineResponseView(Long id, String name, LocalTime startTime,
                            LocalTime endTime, int interval, List<Station> stations, List<Edge> edges) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
        this.stations = stations;
        this.edges = edges;
    }

    public static LineResponseView of(Line line) {
        return LineResponseView.builder()
                .id(line.getId())
                .name(line.getName())
                .startTime(line.getStartTime())
                .endTime(line.getEndTime())
                .interval(line.getIntervalTime())
                .edges(line.getEdges())
                .build();
    }
}
