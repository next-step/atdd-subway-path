package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;

import nextstep.subway.line.exception.NotExistSameStationException;
import nextstep.subway.line.exception.TooLongDistanceException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {
    private static final int MINIMUM_DISTANCE = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.MERGE)
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

    public List<Section> divideSections(Section section) {
        validateDivideSections(section);

        if (hasSameUpStation(section)) {
            return Arrays.asList(
                Section.of(getLine(), getUpStation(), section.getDownStation(), section.getDistance()),
                Section.of(getLine(), section.getDownStation(), getDownStation(), calculateDistance(section))
            );
        }

        return Arrays.asList(
            Section.of(getLine(), getUpStation(), section.getUpStation(), calculateDistance(section)),
            Section.of(getLine(), section.getUpStation(), section.getDownStation(), section.getDistance())
        );
    }

    public boolean containsStation(Section section) {
        return hasSameUpStation(section)
            || upStation.equals(section.getDownStation())
            || downStation.equals(section.getUpStation())
            || hasSameDownStation(section);
    }

    private void validateDivideSections(Section section) {
        if (!hasSameUpStation(section) && !hasSameDownStation(section)) {
            throw new NotExistSameStationException();
        }

        if (calculateDistance(section) == MINIMUM_DISTANCE) {
            throw new TooLongDistanceException();
        }
    }

    private boolean hasSameDownStation(Section section) {
        return downStation.equals(section.getDownStation());
    }

    public boolean isLastSection(Section section) {
        return downStation.equals(section.getUpStation())
            || upStation.equals(section.getDownStation());
    }

    private boolean hasSameUpStation(Section section) {
        return upStation.equals(section.getUpStation());
    }

    private int calculateDistance(Section section) {
        return getDistance() - section.getDistance();
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
}
