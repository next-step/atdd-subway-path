package nextstep.subway.section;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;
    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    private int distance;

    public Section() {
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(null, null, upStation, downStation, distance);
    }

    public Section(final Line line, final Station upStation, final Station downStation, final int distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(final Long id, final Line line, final Station upStation, final Station downStation, final int distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void update(Section section) {
        if (this.distance <= section.distance) {
            throw new BusinessException(String.format("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 합니다. 기존 역 사이 거리 : %s, 입력된 역 사이 거리 : %s", this.distance, section.distance));
        }

        int cache = this.distance;
        this.distance -= section.distance;
        section.distance = cache - this.distance;

        if (this.getUpStation().equals(section.getUpStation())) {
            this.upStation = section.getDownStation();
        } else {
            this.downStation = section.getUpStation();
        }
    }

    public void merge(Station station, int distance) {
        this.downStation = station;
        this.distance = this.distance + distance;
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

    public void updateLine(Line line) {
        this.line = line;
    }
}
