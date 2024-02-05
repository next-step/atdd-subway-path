package nextstep.subway.entity;

import javax.persistence.*;
import java.util.List;

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

	public Line() {

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
		if(!isEndStation(section.getUpStationId())) {
			throw new IllegalArgumentException("노선의 하행 종점역과 구간의 상행역은 같아야 합니다.");
		}

		if(hasStation(section.getDownStationId())) {
			throw new IllegalArgumentException("해당 노선에 " + section.getDownStationId() + "역이 이미 존재합니다.");
		}


		this.endStationId = section.getDownStationId();
		this.distance = this.distance + section.getDistance();
		this.sections.addSection(section);
	}

	public void deleteSection(Long stationId) {
		if(!isEndStation(stationId)) {
			throw new IllegalArgumentException("노선의 하행 종점역만 제거할 수 있습니다.");
		}

		Section section = sections.getSectionByDownStationId(stationId);

		if(isStartStation(section.getUpStationId())) {
			throw new IllegalArgumentException("상행 종점역과 하행 종점역만 있는 노선입니다.");
		}

		this.endStationId = section.getUpStationId();
		this.distance = this.distance - section.getDistance();

		sections.removeSection(section);
	}

	public boolean isEndStation(Long stationId) {
		return endStationId.equals(stationId);
	}

	public boolean isStartStation(Long stationId) {
		return startStationId.equals(stationId);
	}

	public boolean hasStation(Long downStationId) {
		return sections.hasStation(downStationId);
	}
}
