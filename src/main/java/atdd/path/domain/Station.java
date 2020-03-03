package atdd.path.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
public class Station {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
    private List<Edge> edgesAsSource;

    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL)
    private List<Edge> edgesAsTarget;

    private Set<Line> lines;

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

    public void addEdgeToSource(Edge edge){
        if(edgesAsSource == null){
            edgesAsSource = new ArrayList<>();
        }
        edgesAsSource.add(edge);
    }
    public void addEdgeToTarget(Edge edge){
        if(edgesAsTarget == null){
            edgesAsTarget = new ArrayList<>();
        }
        edgesAsTarget.add(edge);
    }

    public void addLine(Line line){
        if(lines == null){
            lines = new HashSet<>();
        }
        lines.add(line);
    }
}

