package nextstep.subway.entity;

import nextstep.subway.dto.SectionResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<Section> sections;

	private Long startStationId;

	private Long endStationId;

	private int distance;

	public Sections() {
	}

	protected Sections(Long startStationId, Long endStationId, int distance) {
		this.sections = new ArrayList<>();
		this.startStationId = startStationId;
		this.endStationId = endStationId;
		this.distance = distance;
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

	public void addSection(Section section) {
		this.sections.add(section);
	}

	public void addSection(Line line, Section section) {
		if (hasStation(section.getDownStationId()) && hasStation(section.getUpStationId())) {
			throw new IllegalArgumentException("해당 노선에 등록할 역들이 이미 존재합니다.");
		}

		if (!hasStation(section.getDownStationId()) && !hasStation(section.getUpStationId())) {
			throw new IllegalArgumentException("등록 구간의 역들이 모두 노선에 존재하지 않습니다.");
		}

		if (isStartStation(section.getDownStationId())) {
			updateStartStation(section.getUpStationId(), section.getDistance());
			addSection(section);

			return;
		}

		if (isEndStation(section.getUpStationId())) {
			updateEndStation(section.getDownStationId(), section.getDistance());
			addSection(section);

			return;
		}

		addMidSection(line, section);
	}

	public void addMidSection(Line line, Section section) {
		Section upSection = getSectionByUpStationId(section.getUpStationId());

		if (upSection.getDistance() <= section.getDistance()) {
			throw new IllegalArgumentException("등록 구간의 길이는 기존 구간의 길이보다 크거나 같을 수 없습니다.");
		}

		addSection(section);
		addSection(new Section(line, section.getDownStationId(), upSection.getDownStationId(), upSection.getDistance() - section.getDistance()));
		deleteSection(upSection);
	}

	public void deleteSection(Section section) {
		if(getSize() <= 1) {
			throw new IllegalArgumentException("상행 종점역과 하행 종점역만 있는 노선입니다.");
		}

		this.sections.remove(section);
	}

	public void deleteSection(Line line, Long stationId) {
		if (isStartStation(stationId)) {
			Section section = getSectionByUpStationId(stationId);
			updateStartStation(section.getDownStationId(), -section.getDistance());

			deleteSection(section);

			return;
		}

		if (isEndStation(stationId)) {
			Section section = getSectionByDownStationId(stationId);
			updateEndStation(section.getUpStationId(), -section.getDistance());

			deleteSection(section);

			return;
		}

		deleteMidSection(line, stationId);

	}

	public void deleteMidSection(Line line, Long stationId) {
		Section upSection = getSectionByUpStationId(stationId);
		Section downSection = getSectionByDownStationId(stationId);

		addSection(new Section(line, downSection.getUpStationId(), upSection.getDownStationId(), upSection.getDistance() + downSection.getDistance()));
		deleteSection(upSection);
		deleteSection(downSection);
	}


	public boolean isEndStation(Long stationId) {
		return endStationId.equals(stationId);
	}

	public boolean isStartStation(Long stationId) {
		return startStationId.equals(stationId);
	}


	public boolean hasStation(Long stationId) {
		for(Section section : sections) {
			if (stationId.equals(section.getDownStationId())) {
				return true;
			}

			if (stationId.equals(section.getUpStationId())) {
				return true;
			}
		}

		return false;
	}

	public Section getSectionByDownStationId(Long downStationId) {
		return sections.stream()
				.filter(x-> downStationId.equals(x.getDownStationId()))
				.findAny()
				.orElseThrow(EntityNotFoundException::new);
	}

	public Section getSectionByUpStationId(Long upStationId) {
		return sections.stream()
				.filter(x-> upStationId.equals(x.getUpStationId()))
				.findAny()
				.orElseThrow(EntityNotFoundException::new);
	}

	public int getSize() {
		return sections.size();
	}

	public List<SectionResponse> convertToSectionResponse() {
		return sections.stream()
				.map(SectionResponse::new)
				.collect(Collectors.toList());
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
