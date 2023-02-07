package nextstep.subway.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public boolean equalId(Long id) {
        return this.id != null && this.id.equals(id);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Station))
            return false;
        Station station = (Station)o;
        return Objects.equals(id, station.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
