package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.apache.commons.lang3.ObjectUtils;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void changeDownStationAndDistance(final Station downStation, final int distance) {
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isSameWithDownStation(Station station) {
        Objects.requireNonNull(station);
        return station.equals(downStation);
    }

    public boolean isSameWithUpStation(Station station) {
        Objects.requireNonNull(station);
        return station.equals(upStation);
    }
}
