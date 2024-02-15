package subway.line;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import subway.station.Station;

@Entity
public class Section implements Comparable<Section> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(targetEntity = Line.class)
	@JoinColumn(name = "line_id", nullable = false)
	private Line line;

	@ManyToOne(targetEntity = Station.class)
	@JoinColumn(name = "up_station_id", nullable = false)
	private Station upStation;

	@ManyToOne(targetEntity = Station.class)
	@JoinColumn(name = "down_station_id", nullable = false)
	private Station downStation;

	@Column(nullable = false)
	private Integer distance;

	protected Section() {
	}

	public Section(Line line, Station upStation, Station downStation, Integer distance) {
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

	public Integer getDistance() {
		return distance;
	}

	public List<Station> getStations() {
		return List.of(upStation, downStation);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Section section = (Section)o;
		return Objects.equals(line, section.line)
			&& Objects.equals(upStation, section.upStation)
			&& Objects.equals(downStation, section.downStation)
			&& Objects.equals(distance, section.distance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(line, upStation, downStation, distance);
	}

	@Override
	public int compareTo(Section next) {
		final int sort = 1;
		Station nextUpStation = next.getUpStation();
		Station nextDownStation = next.getDownStation();

		if (downStation.equals(nextUpStation)) {
			return -sort;
		}

		if (upStation.equals(nextDownStation)) {
			return sort;
		}

		return 0;
	}
}
