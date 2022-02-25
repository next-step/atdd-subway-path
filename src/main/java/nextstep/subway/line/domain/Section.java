package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.line.exception.InvalidDistanceException;
import nextstep.subway.line.exception.InvalidSectionSplitException;
import nextstep.subway.line.exception.SectionMergeFailedException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    private static final int MIN_DISTANCE = 0;

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

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean hasStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean isRegistered(Section section) {
        return hasStation(section.upStation) && hasStation(section.downStation);
    }

    public boolean isSplittable(Section section) {
        return upStation.equals(section.upStation) || downStation.equals(section.downStation);
    }

    public List<Section> split(Section section) {
        if (upStation.equals(section.upStation)) {
            return Arrays.asList(
                    new Section(line, upStation, section.downStation, section.distance),
                    new Section(line, section.downStation, downStation, calculateSplitDistance(section))
            );
        }
        if (downStation.equals(section.downStation)) {
            return Arrays.asList(
                    new Section(line, upStation, section.upStation, calculateSplitDistance(section)),
                    new Section(line, section.upStation, downStation, section.distance)
            );
        }
        throw new InvalidSectionSplitException();
    }

    public Section mergeUpStationWithDownStation(Section nextSection) {
        if (!downStation.equals(nextSection.upStation)) {
            throw new SectionMergeFailedException();
        }
        int mergedDistance = distance + nextSection.distance;
        return new Section(line, upStation, nextSection.downStation, mergedDistance);
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

    private int calculateSplitDistance(Section section) {
        int splitDistance = distance - section.distance;
        if (splitDistance <= MIN_DISTANCE) {
            throw new InvalidDistanceException();
        }
        return splitDistance;
    }
}
