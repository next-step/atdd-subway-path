package atdd.station.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

}
