package atdd.path.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;

    @JsonIgnore
    @Embedded
    private Edges edges = new Edges();

    public Line() {
    }

    @Builder
    public Line(Long id, String name, List<Edge> edges, LocalTime startTime, LocalTime endTime, int interval) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = interval;
        this.edges = new Edges(edges);
    }

    public void changeStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void changeEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void changeInterval(int interval) {
        this.intervalTime = interval;
    }

    public List<Station> getStations() {
        return edges.getStations();
    }

    public void changeEdges(Edges newEdges) {
        this.edges = newEdges;
    }

    public List<Long> findIdOfEdgesToDelete(Station stationToDelete) {
        List<Long> idToDelete = edges.findIdOfEdgesToDelete(stationToDelete);
        return idToDelete;
    }

    public Edges findNewEdges(Station stationToDelete) {
        Edges newEdges = edges.findEdgesAfterRemovalOfStation(stationToDelete);
        return newEdges;
    }
}
