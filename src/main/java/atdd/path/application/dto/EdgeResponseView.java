package atdd.path.application.dto;

import atdd.path.domain.Edge;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class EdgeResponseView {
    private Long id;

    @JsonIgnore
    private Line line;

    private Station source;

    private Station target;

    private int distance;

    private int timeToTake;

    @JsonIgnore
    private List<Edge> edges;

    public EdgeResponseView() {
    }

    @Builder
    public EdgeResponseView(Long id, Line line, Station source, Station target,
                            int distance, int timeToTake, List<Edge> edges) {
        this.id = id;
        this.line = line;
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.timeToTake = timeToTake;
        this.edges = edges;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getSource() {
        return source;
    }

    public Station getTarget() {
        return target;
    }

    public int getDistance() {
        return distance;
    }

    public int getTimeToTake() {
        return timeToTake;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public static EdgeResponseView of(Edge edge) {
        return EdgeResponseView.builder()
                .id(edge.getId())
                .line(edge.getLine())
                .source(edge.getSource())
                .target(edge.getTarget())
                .distance(edge.getDistance())
                .timeToTake(edge.getTimeToTake())
                .build();
    }
}
