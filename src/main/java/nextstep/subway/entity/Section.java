package nextstep.subway.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    public static final int MIN_STATION_ID_VALUE = 1;

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

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = validateDistance(distance);
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

    public boolean isLeastOneSameStation(Station downStation) {
        return (this.upStation.isSame(downStation) || this.downStation.isSame(downStation));
    }

    private int validateDistance(Integer distance) {
        if (distance == null || distance < MIN_DISTANCE_VALUE) {
            throw new IllegalArgumentException("거리는 0보다 커야합니다.");
        }
        return distance;
    }

    public boolean isUpStationSame(Section sectionToAdd) {
        return this.upStation.equals(sectionToAdd.getUpStation());
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

