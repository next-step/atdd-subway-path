package nextstep.subway.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.domain.exception.InvalidLineUpdateException;
import nextstep.subway.domain.exception.LineErrorCode;

@Entity
public class Line {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String color;

	@Column(name = "upStationId")
	private Long upStationId;

	@Column(name = "downStationId")
	private Long downStationId;

	@Embedded
	private final Sections sections = new Sections();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;

		this.upStationId = upStation.getId();
		this.downStationId = downStation.getId();

		this.sections.createInitialLineSection(upStation, downStation, distance, this);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public String getColor() {
		return color;
	}

	public List<Section> getSections() {
		return sections.getList();
	}

	public void addSection(Station upStation, Station downStation, int distance) {
		this.sections.addSection(this, upStation, downStation, distance);
	}

	public List<Station> getStations() {
		return sections.getStations(upStationId, downStationId);
	}

	public void removeSection(Station station) {
		this.sections.remove(this, station, upStationId, downStationId);
	}

	public void updateInfo(String name, String color) {
		validateName(name);
		if (color == null || color.isBlank()) {
			throw new InvalidLineUpdateException(LineErrorCode.INVALID_COLOR_UPDATE_REQUEST);
		}
		this.name = name;
		this.color = color;
	}

	boolean equalFinalDownStation(Station upStation) {
		return this.downStationId.equals(upStation.getId());
	}

	void updateFinalDownStation(Station downStation) {
		this.downStationId = downStation.getId();
	}

	void updateFinalUpStation(Station upStation) {
		this.upStationId = upStation.getId();
	}

	boolean equalFinalUpStation(Station downStation) {
		return this.upStationId.equals(downStation.getId());
	}

	private void validateName(String name) {
		if (name == null || name.isBlank()) {
			throw new InvalidLineUpdateException(LineErrorCode.INVALID_NAME_UPDATER_REQUEST);
		}
	}
}
