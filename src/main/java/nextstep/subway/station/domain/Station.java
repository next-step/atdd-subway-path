package nextstep.subway.station.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE station SET deleted_at = CURRENT_TIMESTAMP where station_id = ?")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long stationId;

    @Column(length = 20, nullable = false)
    private String name;

    @Column
    private LocalDateTime deletedAt;

    protected Station() {
    }

    private Station(String name) {
        this.name = name;
    }

    public static Station from(String name) {
        return new Station(name);
    }

    public Long getStationId() {
        return stationId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Station station = (Station) o;
        return Objects.equals(stationId, station.getStationId()) && Objects.equals(name, station.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, name);
    }

}
