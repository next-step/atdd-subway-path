package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    private static final String DISTANCE_ERROR_MESSAGE = "새로운 구간의 길이가 기존 구간 사이의 길이보다 큽니다.";

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

    public Section() { }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean matchStation(Section section) {
        return anyMatchUpStation(section)
                || anyMatchDownStation(section);
    }

    public boolean anyMatchUpStation(Section section) {
        return upStation.equals(section.getUpStation())
                || upStation.equals(section.getDownStation());
    }

    public boolean isUpStation(Section section) {
        return this.upStation.equals(section.getDownStation());
    }

    public int getDistance() {
        return distance;
    }

    private boolean anyMatchDownStation(Section section) {
        return downStation.equals(section.getUpStation())
                || downStation.equals(section.getDownStation());
    }

    public void changeSection(Section section) {
        validationDistance(section.getDistance());
        this.upStation = section.getUpStation();
        this.distance = this.distance - section.distance;
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

    private void validationDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException(DISTANCE_ERROR_MESSAGE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return getDistance() == section.getDistance() && Objects.equals(getId(), section.getId()) && Objects.equals(getLine(), section.getLine()) && Objects.equals(getUpStation(), section.getUpStation()) && Objects.equals(getDownStation(), section.getDownStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLine(), getUpStation(), getDownStation(), getDistance());
    }
}