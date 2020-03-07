package atdd.line.domain;

import atdd.path.domain.Edges;
import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class Line {

    public static final Line EMPTY_LINE = new Line();

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private int intervalTime;

    @OneToMany(mappedBy = "line")
    private List<Edge> edges = new ArrayList<>();

    @Builder
    protected Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    @Builder(builderMethodName = "testBuilder")
    protected Line(Long id, String name, List<Edge> edges, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this(name, startTime, endTime, intervalTime);
        this.id = id;
        this.edges = edges;
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
        edge.changeLine(this);
    }

    public List<Station> getStations() {
        return new Edges(edges).getStations();
    }

    public Edges removeStation(Station station) {
        return new Edges(edges).removeStation(station);
    }

}
