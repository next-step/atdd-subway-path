package atdd.station.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    public Station() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Station(String name) {
        this.name = name;
    }

    public static Station of(String stationName) {
        return new Station(stationName);
    }
}
