package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    private static final String OVER_DISTANCE_ERROR_MESSAGE = "새로운 구간의 길이가 기존 구간 사이의 길이보다 큽니다.";
    private static final String ZERO_DISTANCE_ERROR_MESSAGE = "구간의 길이는 0 이하가 될 수 없습니다.";
    private static final int DISTANCE_MINIMUM = 1;

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

    protected Section() { }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validationMinimumDistance(distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean matchUpStation(Section newSection) {
        return upStation.equals(newSection.getUpStation())
                || upStation.equals(newSection.getDownStation());
    }

    public boolean matchDownStation(Section newSection) {
        return downStation.equals(newSection.getDownStation())
                || downStation.equals(newSection.getUpStation());
    }

    public boolean isUpStation(Station newDownStation) {
        return upStation.equals(newDownStation);
    }

    public boolean isDownStation(Station newUpStation) {
        return downStation.equals(newUpStation);
    }

    public void changeDownStationToNewUpStations(Section newSection) {
        changeDistance(newSection.getDistance());
        downStation = newSection.getUpStation();
    }

    public void changeUpStationToNewDownStations(Section newSection) {
        changeDistance(newSection.getDistance());
        upStation = newSection.getDownStation();
    }

    private void changeDistance(int newDistance) {
        validationDistance(newDistance);
        this.distance = this.distance - newDistance;
    }

    private void validationDistance(int newDistance) {
        validationMinimumDistance(newDistance);
        validationOverDistance(newDistance);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    private void validationMinimumDistance(int distance) {
        if (distance < DISTANCE_MINIMUM) {
            throw new IllegalArgumentException(ZERO_DISTANCE_ERROR_MESSAGE);
        }
    }

    private void validationOverDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException(OVER_DISTANCE_ERROR_MESSAGE);
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