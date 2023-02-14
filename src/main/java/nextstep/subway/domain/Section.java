package nextstep.subway.domain;

import nextstep.subway.domain.exception.SubwayException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        if (upStation == downStation) {
            throw new SubwayException("상행역과 하행역은 같을 수 없습니다.");
        }

        if (distance <= 0) {
            throw new SubwayException("역 사이의 길이는 0 이상이어야 합니다.");
        }

        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section merge(Section upSection, Section downSection) {
        if (upSection.downStation != downSection.upStation) {
            throw new SubwayException(String.format("%s - %s 구간끼리 연결되어 있지 않습니다.", upSection.downStation.getName(), downSection.upStation.getName()));
        }

        return new Section(upSection.line, upSection.upStation, downSection.downStation, upSection.distance + downSection.distance);
    }

    public Section to(Section newSection) {
        if (isSameUpStation(newSection)) {
            return new Section(line,
                    newSection.downStation,
                    downStation,
                    distance - newSection.distance);
        }

        if (isSameDownStation(newSection)) {
            return new Section(line,
                    upStation,
                    newSection.upStation,
                    distance - newSection.distance);
        }

        throw new SubwayException("구간 사이에 생성할 수 없습니다.");
    }

    public Long getId() {
        return id;
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

    public boolean hasStationId(long stationId) {
        return upStation.getId() == stationId || downStation.getId() == stationId;
    }

    public boolean isSameUpStation(Section other) {
        return upStation == other.upStation;
    }

    public boolean isSameDownStation(Section other) {
        return downStation == other.downStation;
    }
}
