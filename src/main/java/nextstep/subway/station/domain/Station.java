package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {
    private String name;

    public Station() {
    }

    public Station(Long userId, String name) {
        if (userId == null || userId == 0) {
            throw new InvalidStationException();
        }
        if (name.length() < 2 || name.length() > 100) {
            throw new InvalidStationException();
        }
        this.userId = userId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public void update(Station station) {
        this.name = station.getName();
    }
}
