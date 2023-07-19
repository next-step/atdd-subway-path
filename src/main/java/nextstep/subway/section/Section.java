package nextstep.subway.section;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
        int cache = this.distance;
        this.distance -= section.distance;
        section.distance = cache - this.distance;


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
