package nextstep.subway.domain;

import java.util.List;

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

	public Section() {

	}

	public Section(Line line, Station upStation, Station downStation, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public Section(Station upStation, Station downStation, int distance) {
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

	public List<Station> getStationList() {
		return List.of(upStation, downStation);
	}

	public boolean isSameDownStation(Station downStation) {
		return this.downStation.equals(downStation);
	}

	public boolean isSameDownStation(Section section) {
		return isSameDownStation(section.getDownStation());
	}

	public boolean isSameUpStation(Section section) {
		return isSameUpStation(section.getUpStation());
	}

	public boolean isSameUpStation(Station station) {
		return this.upStation.equals(station);
	}

	public boolean isLongerDistance(Section section) {
		return this.distance >= section.getDistance();
	}

	public Station getRemainStation(Station station) {
		if (isSameUpStation(station)) {
			return this.downStation;
		}
		return this.upStation;
	}
}
