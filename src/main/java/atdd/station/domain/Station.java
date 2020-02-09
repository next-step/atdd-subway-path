package atdd.station.domain;

import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 2, max = 20)
    private String name;

    @ManyToMany
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_station_to_subwayLine"))
    private List<SubwayLine> subwayLines = new ArrayList<>();

    private boolean deleted = false;

    public Station() {
    }

    @Builder
    public Station(String name) {
        this.name = name;
    }

    public Station(String name, List<SubwayLine> subwayLines) {
        this.name = name;
        this.subwayLines = subwayLines;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                '}';
    }

    public void deleteStation() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return this.deleted;
    }
}


