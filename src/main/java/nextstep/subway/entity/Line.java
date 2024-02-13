package nextstep.subway.entity;

import javax.persistence.*;

@Entity
public class Line {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String color;

	private Long startStationId;

	private Long endStationId;

	private int distance;

	@Embedded
	private Sections sections;

	protected Line() {

	}

	public Line(String name, String color, Long startStationId, Long endStationId, int distance) {
		this.name = name;
		this.color = color;
		this.startStationId = startStationId;
		this.endStationId = endStationId;
		this.distance = distance;
		this.sections = new Sections();
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
		return startStationId;
	}

	public Long getEndStationId() {
		return endStationId;
	}

	public int getDistance() {
		return distance;
	}

	public Sections getSections() {
		return sections;
	}

	public void setUpdateInfo(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public void addSection(Section section) {
		if (hasStation(section.getDownStationId()) && hasStation(section.getUpStationId())) {
			throw new IllegalArgumentException("해당 노선에 등록할 역들이 이미 존재합니다.");
		}

		if (!hasStation(section.getDownStationId()) && !hasStation(section.getUpStationId())) {
			throw new IllegalArgumentException("등록 구간의 역들이 모두 노선에 존재하지 않습니다.");
		}

		if (isStartStation(section.getDownStationId())) {
			updateStartStation(section.getUpStationId(), section.getDistance());
			sections.addSection(section);

			return;
		}

		if (isEndStation(section.getUpStationId())) {
			updateEndStation(section.getDownStationId(), section.getDistance());
			sections.addSection(section);

			return;
		}

		sections.addMidSection(this, section);
	}

	public void deleteSection(Long stationId) {
		if (isStartStation(stationId)) {
			Section section = sections.getSectionByUpStationId(stationId);
			updateStartStation(section.getDownStationId(), -section.getDistance());
			sections.deleteSection(section);

			return;
		}

		if (isEndStation(stationId)) {
			Section section = sections.getSectionByDownStationId(stationId);
			updateEndStation(section.getUpStationId(), -section.getDistance());
			sections.deleteSection(section);

			return;
		}

		sections.deleteMidSection(this, stationId);
	}

	public boolean isEndStation(Long stationId) {
		return endStationId.equals(stationId);
	}

	public boolean isStartStation(Long stationId) {
		return startStationId.equals(stationId);
	}

	public boolean hasStation(Long stationId) {
		return sections.hasStation(stationId);
	}

	private void updateStartStation(Long stationId, int distance) {
		this.startStationId = stationId;
		this.distance += distance;
	}

	private void updateEndStation(Long stationId, int distance) {
		this.endStationId = stationId;
		this.distance += distance;
	}
}
