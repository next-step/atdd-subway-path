package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
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

    public boolean hasShorterDistanceThan(Section section) {
        return this.distance < section.distance;
    }

    public boolean hasOneMatchedStation(Section section) {
        return this.upStation.equals(section.downStation)
                || this.downStation.equals(section.upStation)
                || this.upStation.equals(section.upStation)
                || this.downStation.equals(section.downStation);
    }

    public boolean hasBothMatchedStation(Section section) {
        return this.upStation.equals(section.upStation) && this.downStation.equals(section.downStation);
    }

    public boolean hasStation(Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }

    public Section divideBy(Section section) {
        if (this.downStation.equals(section.downStation)) {
            return new Section(this.line, this.upStation, section.upStation, this.distance - section.distance);
        }
        return new Section(this.line, section.downStation, this.downStation, this.distance - section.distance);
    }

    public Section merge(Section section) {
        return new Section(this.line, this.upStation, section.getDownStation(), this.distance + section.distance);
    }

    public boolean isDownStationEqualTo(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isUpStationEqualTo(Station station) {
        return this.upStation.equals(station);
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
}
