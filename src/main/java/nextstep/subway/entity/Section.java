package nextstep.subway.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    public static final int MIN_DISTANCE_VALUE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private int distance;

    @ManyToOne
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = validateDistance(distance);
        this.line = line;
    }

    public boolean areStationsSame() {
        return upStation.equals(downStation);
    }

    public Section setLine(Line line) {
        this.line = line;
        line.addSection(this);

        return this;
    }

    public boolean isAtLeastOneSameStation(Station station) {
        return (this.upStation.isSame(station) || this.downStation.isSame(station));
    }

    public boolean isAtLeastOneSameStation(Section section) {
        return isAtLeastOneSameStation(section.getUpStation()) ||
                isAtLeastOneSameStation(section.getDownStation());
    }

    private int validateDistance(Integer distance) {
        if (distance == null || distance < MIN_DISTANCE_VALUE) {
            throw new IllegalArgumentException("거리는 0보다 커야합니다.");
        }
        return distance;
    }

    public Station findCommonStation(Section sectionToAdd) {
        if (sectionToAdd.isAtLeastOneSameStation(upStation)) {
            return this.upStation;
        }
        if (sectionToAdd.isAtLeastOneSameStation(downStation)) {
            return this.downStation;
        }
        return null;
    }

    public boolean canConnectBefore(Section sectionToConnect) {
        return upStation.isSame(sectionToConnect.getDownStation());
    }

    public boolean canConnectNext(Section sectionToConnect) {
        return downStation.isSame(sectionToConnect.getUpStation());
    }

    public Long getId() {
        return id;
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

    public Line getLine() {
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, line);
    }
}

