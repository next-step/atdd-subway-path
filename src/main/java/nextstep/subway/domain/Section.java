package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.SubwayRuntimeException;
import nextstep.subway.exception.message.SubwayErrorCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
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
        validate(upStation, downStation);

        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validate(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new SubwayRuntimeException(SubwayErrorCode.VALIDATE_SECTION_SAME_STATION.getMessage());
        }
    }

    public Section add(Station station, int distance) {
        this.distance = remainDistance(distance);
        Station tempDownStation = this.downStation;
        this.downStation = station;

        return new Section(line, station, tempDownStation, distance);
    }

    private int remainDistance(final int distance) {
        if (distance <= 0 || this.distance <= distance) {
            throw new SubwayRuntimeException(SubwayErrorCode.INVALID_SECTION_DISTANCE.getMessage());
        }

        return this.distance - distance;
    }

    public boolean isSameUpStation(final Section newSection) {
        return this.upStation.equals(newSection.upStation);
    }

    public boolean isSameDownStation(final Section newSection) {
        return this.downStation.equals(newSection.downStation);
    }

    public boolean isContainStation(final Station newSection) {
        return this.upStation.equals(newSection) || this.downStation.equals(newSection);
    }

    public Section merge(Section upSection, Section downSection) {
        if (upSection.downStation != downSection.upStation) {
            throw new SubwayRuntimeException(SubwayErrorCode.NOT_CONNECTED_SECTION.getMessage());
        }

        return new Section(upSection.line, upSection.upStation, downSection.downStation, upSection.distance + downSection.distance);
    }

    public boolean hasStationId(final long stationId) {
        if (upStation.getId() == null || downStation.getId() == null) {
            throw new SubwayRuntimeException(SubwayErrorCode.NOT_FOUND_STATION.getMessage());
        }

        return upStation.getId() == stationId || downStation.getId() == stationId;
    }
}