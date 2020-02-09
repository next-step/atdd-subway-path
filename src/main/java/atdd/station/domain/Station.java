package atdd.station.domain;

import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 2, max = 20)
    private String name;

    private boolean deleted = false;

    public Station() {
    }

    @Builder
    public Station(String name) {
        this.name = name;
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


