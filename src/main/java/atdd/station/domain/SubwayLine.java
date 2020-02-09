package atdd.station.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class SubwayLine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String firstSubwayTime;
    private String lastSubwayTime;

    private Integer dispatchInterval;

    @OneToMany
    private List<Station> stations;

    public SubwayLine() {
    }

    public SubwayLine(String name) {
    }

    public static SubwayLine of(String name) {
        return new SubwayLine(name);
    }

    public Long getId() {
        return id;
    }
}
