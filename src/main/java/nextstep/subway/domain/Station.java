package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    protected Station() {
    }

    private Station(String name) {
        this.name = name;
    }

    public static Station of(String name) {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object target) {
        if (this == target) {
            return true;
        }

        if (target == null || !(target instanceof Station)) {
            return false;
        }

        Station station = (Station) target;

        return id.equals(station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
