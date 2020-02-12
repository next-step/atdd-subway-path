package atdd.station.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class SubwayLine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String firstSubwayTime;
    private String lastSubwayTime;

    private Integer dispatchInterval;

    public SubwayLine() {
    }

    private SubwayLine(String name) {
        this.name = name;
    }

    public static SubwayLine of(String name) {
        return new SubwayLine(name);
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
        if (o == null || getClass() != o.getClass()) return false;
        SubwayLine that = (SubwayLine) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
