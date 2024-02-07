package subway.line;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import subway.station.Station;

@Entity
public class Section {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(targetEntity = Line.class, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "line_id", nullable = false)
	private Line line;

	@ManyToOne(targetEntity = Station.class, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "up_station_id", nullable = false)
	private Station upStation;

	@ManyToOne(targetEntity = Station.class, cascade = CascadeType.PERSIST)
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
}
