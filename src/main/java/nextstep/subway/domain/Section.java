package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

    public boolean isDuplicateStation(Station upStation, Station downStation) {
        return isEqualSection(upStation, downStation) || isEqualSection(downStation, upStation);
    }

    public boolean isContainStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean isUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    public boolean isGraterOrEqualThanExistingDistance(int distance) {
        return this.distance <= distance;
    }

    private boolean isEqualSection(Station upStation, Station downStation) {
        return this.upStation.equals(upStation) && this.downStation.equals(downStation);
    }
}