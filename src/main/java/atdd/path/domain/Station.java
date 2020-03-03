package atdd.path.domain;

import atdd.path.application.dto.StationResponseView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@ToString(of = {"id", "name"})
public class Station {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "station_id")
    private Long id;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "source")
    private List<Edge> edgesAsSource = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "target")
    private List<Edge> edgesAsTarget = new ArrayList<>();

    @JsonIgnore
    @Transient
    private List<Line> lines = new ArrayList<>();

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    @Builder
    public Station(Long id, String name, List<Edge> edgesAsSource, List<Edge> edgeAsTarget) {
        this.id = id;
        this.name = name;
        this.edgesAsSource = edgesAsSource;
        this.edgesAsTarget = edgeAsTarget;
    }

    public void addEdgeToSource(Edge edge) {
        if (edgesAsSource == null) {
            edgesAsSource = new ArrayList<>();
        }
        this.edgesAsSource.add(edge);
    }

    public void addEdgeToTarget(Edge edge) {
        if (edgesAsTarget == null) {
            edgesAsTarget = new ArrayList<>();
        }
        this.edgesAsTarget.add(edge);
    }

    public void addLine(Line line) {
        if (lines == null) {
            lines = new ArrayList<>();
        }
        this.lines.add(line);
    }
//
//    public List<Line> getLines() {
//        return Stream.concat(edgesAsSource.stream(), edgesAsTarget.stream())
//                .map(it -> it.getLine())
//                .collect(Collectors.toList());
//    }

    public static Station of(StationResponseView responseView) {
        return Station.builder()
                .id(responseView.getId())
                .name(responseView.getName())
                .build();
    }
}

