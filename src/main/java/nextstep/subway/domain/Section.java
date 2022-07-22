package nextstep.subway.domain;

import static nextstep.subway.common.exception.errorcode.EntityErrorCode.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.exception.BusinessException;

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
	private SectionDistance distance;

	@Embedded
	private SectionIndex sectionIndex = new SectionIndex();

	public Section() {

	}

	public Section(Line line, Station upStation, Station downStation, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = new SectionDistance(distance);
	}

	public List<Station> getStation(long index) {
		if (index == 1) {
			return Arrays.asList(this.upStation, this.downStation);
		}
		return Arrays.asList(this.downStation);
	}

	public List<Station> getStationAll() {
		return Arrays.asList(this.upStation, this.downStation);
	}

	public boolean isSameWithDownStation(Station station) {
		return this.downStation.equals(station);
	}

	public boolean isSameWithDownStation(long stationId) {
		return this.downStation.getId() == stationId;
	}

	public boolean isSameWithUpStation(Station station) {
		return this.upStation.equals(station);
	}

	public boolean isSameWithUpStation(long stationId) {
		return this.upStation.getId() == stationId;
	}

	public boolean isSameSectionExists(Station addUpStation, Station addDownStation) {
		if (this.upStation.equals(addUpStation) && this.downStation.equals(addDownStation)) {
			return true;
		}
		return false;
	}

	public boolean isExistAnyStations(Station addUpStation, Station addDownStation) {
		if (this.upStation.equals(addUpStation) || this.upStation.equals(addDownStation)
			|| this.downStation.equals(addUpStation) || this.downStation.equals(addDownStation)) {
			return true;
		}
		return false;
	}

	public void nullValidationOfStations() {
		if (this.upStation == null || this.downStation == null) {
			throw new BusinessException(ENTITY_NOT_FOUND);
		}
	}

	public void changeUpStation(Station station) {
		this.upStation = station;
	}

	public void changeDownStation(Station station) {
		this.downStation = station;
	}

	public void calculateIndex(int index) {
		this.sectionIndex.calculateIndex(index);
	}

	public int getIndex() {
		return sectionIndex.getIndex();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line,
			section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation,
			section.downStation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, line, upStation, downStation, distance);
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
		return this.downStation;
	}

	public int getDistance() {
		return this.distance.getDistance();
	}

}