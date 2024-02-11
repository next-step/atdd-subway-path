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

	public void removeSection(Section section) {
		this.sections.remove(section);
	}

	public boolean hasStation(Long stationId) {
		for(Section section : sections) {
			if(stationId.equals(section.getDownStationId())) {
				return true;
			}

			if(stationId.equals(section.getUpStationId())) {
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
