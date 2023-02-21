package nextstep.subway.domain;

import nextstep.subway.domain.exception.SectionExceptionMessages;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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

    public List<Station> stations() {
        return List.of(upStation, downStation);
    }

    public Section merge(Section section) {
        return new Section(line, upStation, section.getDownStation(), distance + section.getDistance());
    }

    public List<Section> divide(Section section) {
        if (section.getDistance() >= distance) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.INVALID_DISTANCE);
        }

        List<Section> sections = new ArrayList<>();
        sections.add(section);

        if (downStation.equals(section.getDownStation())) {
            sections.add(new Section(line, upStation, section.getUpStation(), distance - section.getDistance()));
        }

        if (upStation.equals(section.getUpStation())) {
            sections.add(new Section(line, section.getDownStation(), downStation, distance - section.getDistance()));
        }

        return sections;
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
        return Objects.equals(id, section.id) &&
                Objects.equals(line, section.line) &&
                Objects.equals(upStation, section.upStation) &&
                Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation);
    }
}