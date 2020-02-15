package atdd.line.domain;

import atdd.station.domain.Station;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "line_station")
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "station_id")
    private Station station;

    protected LineStation() {
    }

    public LineStation(Line line, Station station) {
        Assert.notNull(line, "line 은 필수값 입니다.");
        Assert.notNull(station, "station 은 필수값 입니다.");
        this.line = line;
        this.station = station;
    }

    public boolean isEqualStation(String name) {
        return Objects.equals(name, station.getName());
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getStation() {
        return station;
    }

}
