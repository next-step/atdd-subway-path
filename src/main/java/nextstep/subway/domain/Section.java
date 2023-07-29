package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validate(line, upStation, downStation, distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validate(Line line, Station upStation, Station downStation, int distance) {
        if (line == null || upStation == null || downStation == null) {
            throw new IllegalArgumentException();
        }
        if (distance == 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && line.equals(section.line) && upStation.equals(section.upStation) && downStation.equals(section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, distance);
    }

    public boolean equalDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean equalUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public void updateDownStation(Section section) {
        updateDistance(section);
        this.upStation = section.downStation;
    }

    public void updateUpStation(Section section) {
        updateDistance(section);
        this.downStation = section.upStation;
    }

    private void updateDistance(Section section) {
        if (this.distance <= section.distance) {
            throw new SectionAddException(ErrorType.SECTION_DISTANCE_TOO_LONG);
        }
        this.distance -= section.distance;
    }

    public void removeStation(Section section) {
        this.downStation = section.downStation;
        this.distance += section.distance;
    }
}