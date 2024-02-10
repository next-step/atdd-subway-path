package nextstep.subway.station;

import javax.persistence.*;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
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

    public boolean isEquals(Station station) {
        return this.id.equals(station.getId());
    }

    public boolean isEquals(Long stationId) {
        return this.id.equals(stationId);
    }
}
