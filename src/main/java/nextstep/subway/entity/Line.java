package nextstep.subway.entity;

import javax.persistence.*;

@Entity
public class Line {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String color;

	@Embedded
	private Sections sections;

	protected Line() {

	}

	public Line(String name, String color, Long startStationId, Long endStationId, int distance) {
		this.name = name;
		this.color = color;
		this.sections = new Sections(startStationId, endStationId, distance);
		sections.addSection(new Section(this, startStationId, endStationId, distance));
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public Long getStartStationId() {
		return sections.getStartStationId();
	}

	public Long getEndStationId() {
		return sections.getEndStationId();
	}

	public int getDistance() {
		return sections.getDistance();
	}

	public Sections getSections() {
		return sections;
	}

	public void setUpdateInfo(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public void addSection(Section section) {
		sections.addSection(this, section);
	}

	public void deleteSection(Long stationId) {
		sections.deleteSection(this, stationId);
	}

	public boolean hasStation(Long stationId) {
		return sections.hasStation(stationId);
	}
}
