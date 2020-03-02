package atdd.path.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "sourceStation")
    private List<Edge> sourceEdges = new ArrayList();

    @JsonIgnore
    @OneToMany(mappedBy = "targetStation")
    private List<Edge> targetEdge = new ArrayList();

    @Builder
    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<Line> getLines() {
        return Stream.concat(sourceEdges.stream(), targetEdge.stream())
                .map(it -> it.getLine())
                .collect(Collectors.toList());
    }
}

