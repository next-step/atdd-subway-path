package nextstep.subway.line.domain;

import nextstep.subway.line.exception.SectionException;
import nextstep.subway.station.Station;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Line line;

    public Section() {
    }

    public Section(int distance, Station upStation, Station downStation, Line line) {
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public void changeUpStation(Station upStation, int distance) {
        this.upStation = upStation;
        renewDistance(this.distance - distance);
    }

    private void renewDistance(int distance) {
        if (distance < 1) {
            throw new SectionException("기존구간의 거리보다 더 길수 없습니다.");
        }
        this.distance = distance;
    }
}
