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

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public boolean isEnoughDistance(int distance) {
        return this.distance > distance;
    }

    public boolean isBetweenSection(Section section) {
        return this.upStation.equals(section.upStation) || this.downStation.equals(section.downStation);
    }

    public boolean isLeafSection(Section section) {
        return this.upStation.equals(section.downStation) || this.downStation.equals(section.upStation);
    }

    public void changeUpSection(Section newSection) {
        this.downStation = newSection.upStation;
        this.distance = minusDistance(newSection.distance);
    }

    public void changeDownSection(Section newSection) {
        this.upStation = newSection.downStation;
        this.distance = minusDistance(newSection.distance);
    }

    public int minusDistance(int distance) {
        return this.distance - distance;
    }

    public boolean isSameUpStationForDown(Section section) {
        return this.upStation.equals(section.downStation);
    }

    public boolean isSameStations(Section section) {
        return this.upStation.equals(section.upStation) && this.downStation.equals(section.downStation);
    }

    public boolean isSameUpStationIn(Section section) {
        return this.upStation.equals(section.upStation);
    }
}