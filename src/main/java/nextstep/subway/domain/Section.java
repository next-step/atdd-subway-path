package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Column(nullable = false)
    private Long distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Long distance) {
        this(0L, line, upStation, downStation, distance);
    }

    public Section(Long id, Line line, Station upStation, Station downStation, Long distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isSameLine(Line line) {
        return this.line.equals(line);
    }

    public boolean isUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public List<Station> stations() {
        return List.of(upStation, downStation);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public void changeUpStation(Station station, Long distance) {
        this.upStation = station;
        this.distance -= distance;
    }

    public void changeDownStation(Station station, Long distance) {
        this.downStation = station;
        this.distance -= distance;
    }

    public Long id() {
        return id;
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public Long distance() {
        return distance;
    }


}
