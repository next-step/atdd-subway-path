package nextstep.subway.section;

import java.util.NoSuchElementException;

import javax.persistence.*;

import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

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

    public Section() {

    }

	public static Section createSection(Line line, Station upStation, Station downStation, int distance) {
		if (line.isEmptySection()) {
			return new Section(line, upStation, downStation, distance);
		}

		return new Section(line, upStation, downStation, distance);
	}

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

	public void updateUpStation(Station upStation, int newDistance) {
		this.upStation = upStation;
		this.distance = newDistance;
	}

	public void updateDownStation(Station downStation, int newDistance) {
		this.downStation = downStation;
		this.distance = newDistance;
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
