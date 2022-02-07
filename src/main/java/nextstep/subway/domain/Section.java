package nextstep.subway.domain;

import nextstep.subway.error.exception.InvalidValueException;

import javax.persistence.*;

@Entity
public class Section implements Comparable<Section> {
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

    public boolean doesContains(Station station) {
        return station.equals(upStation) ||
                station.equals(downStation);
    }

    public boolean isNextSection(Station downStation) {
        return upStation.equals(downStation);
    }

    public void changeUpStationWhenAdd(Section section) {
        upStation = section.getDownStation();
        changeDistance(distance - section.distance);
    }

    public void changeUpStationWhenRemove(Section section) {
        upStation = section.getUpStation();
        changeDistance(distance + section.distance);
    }

    private void changeDistance(int distance) {
        if (distance < 1) {
            throw new InvalidValueException();
        }
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

    @Override
    public int compareTo(Section section) {
        if (downStation.equals(section.getUpStation())) {
            return 1;
        }
        return 0;
    }
}