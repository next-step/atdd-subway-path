package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validateNegativeDistance(distance);
        this.line =  Objects.requireNonNull(line, "line must not be null");
        this.upStation = Objects.requireNonNull(upStation, "upStation must not be null");
        this.downStation = Objects.requireNonNull(downStation, "downStation must not be null");
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

    public boolean hasStation(Section section) {
        return equalUpStation(section.upStation) || equalDownStation(section.downStation);
    }

    public boolean hasAnyStation(Section section) {
        return equalUpStation(section.upStation) || equalUpStation(section.downStation)
                || equalDownStation(section.upStation) || equalDownStation(section.downStation);
    }

    public boolean equalDownStation(Station station) {
        return this.downStation == station;
    }

    public boolean equalUpStation(Station station) {
        return this.upStation == station;
    }

    public void updateStation(Section section) {
        if (equalUpStation(section.upStation)) {
            updateUpStation(section.downStation, section.distance);
        }
        if (equalDownStation(section.downStation)) {
            updateDownStation(section.upStation, section.distance);
        }
    }

    public void updateUpStation(Station station, int distance) {
        validateDistance(distance);
        this.upStation = station;
        this.distance -= distance;
    }

    public void updateDownStation(Station station, int distance) {
        validateDistance(distance);
        this.downStation = station;
        this.distance -= distance;
    }

    private void validateDistance(int distance) {
        validateNegativeDistance(distance);
        validateDistanceCompareOtherDistance(distance);
    }

    private void validateDistanceCompareOtherDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("등록할 구간의 길이가 등록된 구간 길이보다 작아야 합니다.");
        }
    }

    private void validateNegativeDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("등록하고자 하는 구간의 길이가 0 이거나 음수일 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        if (distance != section.distance) return false;
        if (!Objects.equals(id, section.id)) return false;
        if (!Objects.equals(line, section.line)) return false;
        if (!Objects.equals(upStation, section.upStation)) return false;
        return Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (line != null ? line.hashCode() : 0);
        result = 31 * result + (upStation != null ? upStation.hashCode() : 0);
        result = 31 * result + (downStation != null ? downStation.hashCode() : 0);
        result = 31 * result + distance;
        return result;
    }
}