package nextstep.subway.domain;

import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Entity
@EqualsAndHashCode(of = "id")
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

    public Line line() {
        return line;
    }

    public boolean isSame(Station upStation, Station downStation) {
        return (this.isUpStation(upStation) && this.isDownStation(downStation)) ||
                (this.isUpStation(downStation) && this.isDownStation(upStation));
    }

    public Long distance() {
        return distance;
    }

}
