package nextstep.subway.domain;

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

    public void updateUpStationToDownStationOf(Section newSection) {
        this.upStation = newSection.downStation;
        this.distance = distance - newSection.distance;
    }

    public void updateDownStationToUpStationWhenAdd(Section newSection) {
        this.downStation = newSection.upStation;
        this.distance = distance - newSection.distance;
    }

    public void updateDownStationToUpStationWhenRemove(Section newSection) {
        this.downStation = newSection.downStation;
        this.distance = distance + newSection.distance;
    }

    public boolean sameOrBiggerThen(Section section) {
        return this.distance >= section.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return getDistance() == section.getDistance()
                && getLine().equals(section.getLine())
                && getUpStation().equals(section.getUpStation())
                && getDownStation().equals(section.getDownStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLine(), getUpStation(), getDownStation(), getDistance());
    }
}