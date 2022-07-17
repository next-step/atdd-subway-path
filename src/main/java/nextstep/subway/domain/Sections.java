package nextstep.subway.domain;

import static nextstep.subway.common.exception.errorcode.EntityErrorCode.*;
import static nextstep.subway.common.exception.errorcode.StatusErrorCode.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.springframework.util.CollectionUtils;

import nextstep.subway.common.exception.BusinessException;

public class Sections {
	@OneToMany(mappedBy = "line",
		cascade = {CascadeType.PERSIST, CascadeType.MERGE},
		orphanRemoval = true)
	@OrderBy("id asc")
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public List<Section> getSections() {
		return Collections.unmodifiableList(sections);
	}

	public List<Station> getStations() {

		if (CollectionUtils.isEmpty(sections) || sections.size() <= 0) {
			Collections.emptyList();
		}

		List<Station> stations = new ArrayList<>();
		int indexOfSections = 1;
		for (Section section : sections) {
			stations.addAll(section.getStation(indexOfSections++));
		}

		return Collections.unmodifiableList(stations);
	}

	public void add(Section addSection) {
		this.sections.add(addSection);
	}

	public void remove(Station station) {

		Section lastSection = getLastSection();
		if (!lastSection.isSameWithDownStation(station.getId())) {
			throw new BusinessException(INVALID_STATUS);
		}

		sections.removeIf(section -> section.equals(lastSection));
	}

	private Section getLastSection() {
		return sections.stream()
			.skip(sections.size() - 1)
			.findFirst()
			.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));

	}
}
