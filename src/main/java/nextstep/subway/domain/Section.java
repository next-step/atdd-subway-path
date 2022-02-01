package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    private static final int DISTANCE_MINIMUM_CONDITION = 1;

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
        validateLine(line);
        validateUpStationId(upStation);
        validateDownStationId(downStation);
        validateDistance(distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateLine(final Line line) {
        if (Objects.isNull(line)) {
            throw new IllegalArgumentException("section line is not valid");
        }
    }

    private void validateUpStationId(final Station upStation) {
        if (Objects.isNull(upStation)) {
            throw new IllegalArgumentException("section upStation is not valid");
        }
    }

    private void validateDownStationId(final Station downStation) {
        if (Objects.isNull(downStation)) {
            throw new IllegalArgumentException("section downStation is not valid");
        }
    }

    private void validateDistance(final int distance) {
        if (distance < DISTANCE_MINIMUM_CONDITION) {
            throw new IllegalArgumentException("section distance is not valid");
        }
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
