package nextstep.subway.domain.line.section;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.LineExceptionCode;
import nextstep.subway.exception.SectionExceptionCode;
import nextstep.subway.exception.StationExceptionCode;
import nextstep.subway.exception.SubwayException;

@Entity
@NoArgsConstructor
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

    public Section(Line line, Station upStation, Station downStation, int distance) {
        throwIfNullIncluded(line, upStation, downStation);
        throwIfContainsInvalidDistance(upStation, downStation, distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isSuperSetOf(Section subSetSection) {
        if (this.isSectionEquals(subSetSection)) {
            return false;
        }

        if (
            this.upStation.equals(subSetSection.upStation) ||
            this.downStation.equals(subSetSection.downStation)
        ) {
            return true;
        }

        return false;
    }

    public boolean isSectionEquals(Section newSection) {
        // 정방향
        if (newSection.getUpStation().equals(this.upStation) &&
            newSection.getDownStation().equals(this.downStation)) {
            return true;
        }

        // 역방향
        if (newSection.getDownStation().equals(this.upStation) &&
            newSection.getUpStation().equals(this.downStation)) {
            return true;
        }
        return false;
    }

    public boolean isUpStationEquals(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isDownStationEquals(Station station) {
        return this.downStation.equals(station);
    }

    public boolean containsStation(Station station) {
        if (this.isUpStationEquals(station)) {
            return true;
        }

        if (this.isDownStationEquals(station)) {
            return true;
        }

        return false;
    }

    public void interposeSectionAtDownStation(Section newSection) {
        this.downStation = newSection.upStation;
        this.distance -= newSection.distance;
        if (distance <= 0) {
            throw new SubwayException(SectionExceptionCode.EXCEED_MAXIMUM_DISTANCE);
        }
    }

    public void interposeSectionAtUpStation(Section newSection) {
        this.upStation = newSection.downStation;
        this.distance -= newSection.distance;
        if (distance <= 0) {
            throw new SubwayException(SectionExceptionCode.EXCEED_MAXIMUM_DISTANCE);
        }
    }

    private void throwIfNullIncluded(Line line, Station upStation, Station downStation) {
        if (line == null) {
           throw new SubwayException(LineExceptionCode.LINE_NOT_FOUND);
        }

        if (upStation == null || downStation == null) {
           throw new SubwayException(StationExceptionCode.STATION_NOT_FOUND, "구간의 상행역 또는 하행역이 존재하지 않습니다.");
        }
    }

    private void throwIfContainsInvalidDistance(Station upStation, Station downStation, int distance) {
        // 시작역은 예외처리
        if (upStation.equals(downStation) && distance == 0) {
            return;
        }

        if (distance <= 0) {
            throw new SubwayException(SectionExceptionCode.ONLY_POSITIVE_DISTANCE_ALLOWED);
        }
    }
}
