package nextstep.subway.domain;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.SecetionExceptionCode;
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
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isStartSection() {
        return this.upStation.equals(this.downStation);
    }

    public Section convertToStartSection() {
        return new Section(this.getLine(), this.getUpStation(), this.getUpStation(), 0);
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

    public void interposeSectionAtDownStation(Section newSection) {
        this.downStation = newSection.upStation;
        this.distance -= newSection.distance;
        if (distance <= 0) {
            throw new SubwayException(SecetionExceptionCode.EXCEED_MAXIMUM_DISTANCE);
        }
    }

    public void interposeSectionAtUpStation(Section newSection) {
        this.upStation = newSection.downStation;
        this.distance -= newSection.distance;
        if (distance <= 0) {
            throw new SubwayException(SecetionExceptionCode.EXCEED_MAXIMUM_DISTANCE);
        }
    }
}
