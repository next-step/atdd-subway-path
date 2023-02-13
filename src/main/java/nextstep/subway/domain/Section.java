package nextstep.subway.domain;

import nextstep.subway.domain.exceptions.CanNotMergeSectionException;
import nextstep.subway.domain.exceptions.CanNotSplitSectionException;

import javax.persistence.*;

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

    @Embedded
    private Distance distance;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section split(Section that) {
        if (isSameUpStation(that) && !isSameDownStation(that)) {
            return new Section(line, that.getDownStation(), this.downStation, distance.minus(that.distance));
        }

        if (!isSameUpStation(that) && isSameDownStation(that)) {
            return new Section(line, upStation, that.upStation, distance.minus(that.distance));
        }

        throw new CanNotSplitSectionException("상행역과 하행역 둘 중 하나만 같아야 함");
    }

    public Section merge(Section that) {
        if (this.downStation.equals(that.upStation)) {
            return new Section(
                    this.line,
                    this.upStation,
                    that.downStation,
                    this.distance.plus(that.distance)
            );
        }

        if (this.upStation.equals(that.downStation)) {
            return new Section(
                    this.line,
                    that.upStation,
                    this.downStation,
                    this.distance.plus(that.distance)
            );
        }

        throw new CanNotMergeSectionException("두 구간이 연결되지 않아 합칠 수 없음");
    }

    private boolean isSameUpStation(Section that) {
        return this.upStation.equals(that.upStation);
    }

    private boolean isSameDownStation(Section that) {
        return this.downStation.equals(that.downStation);
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

    public Distance getDistance() {
        return distance;
    }
}
