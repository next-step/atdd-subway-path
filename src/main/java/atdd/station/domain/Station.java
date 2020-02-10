package atdd.station.domain;

import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 2, max = 20)
    private String name;

    @Embedded
    private SubwayLines subwayLines;

    private boolean deleted = false;

    public Station() {
    }

    @Builder
    public Station(String name) {
        this.name = name;
    }

    public Station(String name, SubwayLines subwayLines) {
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", subwayLines=" + subwayLines +
                '}';
    }

    public void deleteStation() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return this.deleted;
    }
}


