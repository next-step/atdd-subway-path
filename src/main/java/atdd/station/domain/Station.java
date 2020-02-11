package atdd.station.domain;

import atdd.edge.Edge;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "line")
    private List<Edge> edges = new ArrayList();

    @Builder
    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
