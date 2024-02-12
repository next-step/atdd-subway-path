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

	protected Sections() {
		this.sections = new ArrayList<>();
	}

	public void addSection(Section section) {
		this.sections.add(section);
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

}
