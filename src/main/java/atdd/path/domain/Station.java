package atdd.path.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "sourceStation")
    private List<Edge> sourceEdges;

    @OneToMany(mappedBy = "targetStation")
    private List<Edge> targetEdges;

    @OneToMany(mappedBy = "station")
    private List<StationLine> stationLines;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    @Builder
    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

