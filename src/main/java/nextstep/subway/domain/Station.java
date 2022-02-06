package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (this.name == null || o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;
        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public List<Station> findShortestPathTo(
            GraphService graphService,
            Station targetStation,
            List<Line> allLines
    ) {
        return graphService.getShortestPath(this, targetStation, allLines);
    }

    public int findShortestDistanceTo(
            GraphService graphService,
            Station targetStation,
            List<Line> allLines
    ) {
        return graphService.getShortestPathDistance(this, targetStation, allLines);
    }
}
