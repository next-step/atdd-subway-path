package nextstep.subway.domain;

import static nextstep.subway.exception.CommonExceptionMessages.ALREADY_HAS_STATIONS;
import static nextstep.subway.exception.CommonExceptionMessages.INVALID_SECTION_DISTANCE;
import static nextstep.subway.exception.CommonExceptionMessages.NOT_HAS_ANY_STATIONS;

import java.util.Objects;
import javax.persistence.*;
import org.springframework.dao.DataIntegrityViolationException;

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

    private Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(null, upStation, downStation, distance);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setLine(Line line) {
        this.line = line;

        if (this.line.alreadyHas(this)) {
            return;
        }

        this.line.addSection(this);
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public boolean hasSameUpStationWith(Section section) {
        return this.upStation.equals(section.getUpStation());
    }

    public boolean hasSameDownStationWith(Section section) {
        return this.downStation.equals(section.getDownStation());
    }

    public boolean hasStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id)
            && Objects.equals(line, section.line) && Objects.equals(upStation,
            section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    public void checkEnoughDistanceForAddingOrElseThrow(Section section) {
        if (this.distance > section.distance) {
            return;
        }

        throw new DataIntegrityViolationException(INVALID_SECTION_DISTANCE);
    }

    public Line getLine() {
        return line;
    }
}