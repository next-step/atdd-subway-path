package atdd.station.domain;

import atdd.edge.Edge;
import atdd.line.Line;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "line")
    private List<Edge> edges = new ArrayList();

    @Builder
    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<Line> getLines() {
        return this.edges.stream()
                .map(Edge::getLine)
                .collect(Collectors.toList());
    }
}

