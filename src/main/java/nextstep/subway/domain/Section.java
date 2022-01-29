package nextstep.subway.domain;

import nextstep.subway.exception.IllegalSectionArgumentException;

import javax.persistence.*;

@Entity
public class Section {
    public static final String NOT_NULL = "distance 값이 없거나 0으로 올수 없습니다.";
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

    protected Section() {}

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validateDistance(distance);

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

    public boolean isLast(Station station) {
        return this.downStation.equals(station);
    }

    private void validateDistance(int distance) {
        if (distance == 0) {
            throw new IllegalSectionArgumentException(NOT_NULL);
        }
    }
}