package nextstep.subway.domain;

import nextstep.subway.exception.InSectionDistanceException;

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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {

    }

    Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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


    public boolean hasStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean hasSameUpStation(Section section) {
        if (upStation == null) {
            return false;
        }
        return upStation.equals(section.getUpStation());
    }

    public boolean hasSameDownStation(Section section) {
        if (downStation == null) {
            return false;
        }
        return downStation.equals(section.getDownStation());
    }

    public int betweenDistance(Section section) {
        int between = distance - section.getDistance();
        if (between <= 0) {
            throw new InSectionDistanceException("추가 될 구간의 거리가 크거나 같을 수 없습니다.");
        }
        return between;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}