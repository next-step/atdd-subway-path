package nextstep.subway.domain;

import static nextstep.subway.common.exception.errorcode.EntityErrorCode.*;
import static nextstep.subway.common.exception.errorcode.StatusErrorCode.*;
import static nextstep.subway.domain.KindOfAddition.*;

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
	@OrderBy("sectionIndex asc")
	private List<Section> values = new ArrayList<>();

	public Sections() {
	}

	public List<Section> getSections() {
		return Collections.unmodifiableList(values);
	}

	public List<Station> getStations() {

		if (values.isEmpty()) {
			Collections.emptyList();
		}

		List<Station> stations = new ArrayList<>();
		int indexOfSections = 1;
		for (Section section : values) {
			stations.addAll(section.getStation(indexOfSections++));
		}

		return Collections.unmodifiableList(stations);
	}

	public void add(Section addSection) throws BusinessException {

		validationOfSameSection(addSection);
		validationOfAnySameStations(addSection);

		KindOfAddition kindOfAddition = getKindOfAddition(addSection);

		if (kindOfAddition.isSimpleIndex()) {
			this.values.add(getSectionIndexForAdd(kindOfAddition), addSection);
		}

		if (kindOfAddition == ADD_TO_IN_EXISTS_SECTION) {
			Section baseSection = this.values
				.stream()
				.filter(section -> section.isSameWithUpStation(addSection.getUpStation()))
				.findFirst()
				.orElseThrow(() -> new BusinessException(INVALID_STATUS));
			this.values.add(this.values.indexOf(baseSection), addSection);
			baseSection.changeUpStation(addSection.getDownStation());
		}
		calculateSectionIndex();

	}

	private int getSectionIndexForAdd(KindOfAddition kindOfAddition) {

		if (kindOfAddition == ADD_TO_END_OF_DOWN_STATION) {
			return this.values.size();
		}
		return 0;
	}

	private KindOfAddition getKindOfAddition(Section addSection) {
		Section firstSection = CollectionUtils.firstElement(this.values);
		Section lastSection = CollectionUtils.lastElement(this.values);

		if (this.values.isEmpty()) {
			return ADD_TO_END_OF_DOWN_STATION;
		}
		if (addSection.getUpStation().equals(lastSection.getDownStation())) {
			return ADD_TO_END_OF_DOWN_STATION;
		}
		if (addSection.getDownStation().equals(firstSection.getUpStation())) {
			return ADD_TO_TOP_OF_UP_STATION;
		}

		return ADD_TO_IN_EXISTS_SECTION;
	}

	private void validationOfSameSection(Section addSection) {
		if (this.values
			.stream()
			.anyMatch(section -> section.isSameSectionExists(addSection.getUpStation(), addSection.getDownStation()))
		) {
			throw new BusinessException(INVALID_STATUS);
		}
	}

	private void validationOfAnySameStations(Section addSection) {

		if (this.values.isEmpty()) {
			return;
		}
		if (!this.values
			.stream()
			.anyMatch(section -> section.isExistAnyStations(addSection.getUpStation(), addSection.getDownStation()))
		) {
			throw new BusinessException(INVALID_STATUS);
		}
	}

	private void calculateSectionIndex() {
		int sectionIndex = 1;
		for (Section section : this.values) {
			section.calculateIndex(sectionIndex++);
		}
	}

	public void remove(Station station) {

		Section lastSection = getLastSection();
		if (!lastSection.isSameWithDownStation(station)) {
			throw new BusinessException(INVALID_STATUS);
		}

		values.removeIf(section -> section.equals(lastSection));
	}

	private Section getLastSection() {
		return values.stream()
			.skip(values.size() - 1L)
			.findFirst()
			.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));

	}
}
