package nextstep.subway.domain;

import static java.util.stream.Collectors.*;
import static nextstep.subway.common.exception.errorcode.StatusErrorCode.*;
import static nextstep.subway.domain.KindOfAddition.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
			return Collections.emptyList();
		}

		Section topSection = findTopSection(findFirstTopUpStationId(findAllStations()));
		List<Section> newSections = new ArrayList<>();

		for (Section section : this.values) {
			newSections.add(topSection);
			topSection = findNextSection(topSection);
		}

		return getStationByOrder(newSections);
	}

	private List<Station> findAllStations() {
		List<Station> stations = new ArrayList<>();
		this.values
			.forEach(section -> stations.addAll(section.getStationAll()));
		return stations;
	}

	private long findFirstTopUpStationId(List<Station> stations) {
		return getCountPerStation(stations)
			.stream()
			.filter(key -> key.getValue() == 1)
			.filter(entry -> this.values
				.stream()
				.anyMatch(section -> section.isSameWithUpStation(entry.getKey())))
			.findFirst()
			.orElseThrow(() -> new BusinessException(INVALID_STATUS))
			.getKey();
	}

	private long findFirstBottomDownStationId(List<Station> stations) {
		return getCountPerStation(stations)
			.stream()
			.filter(key -> key.getValue() == 1)
			.filter(entry -> this.values
				.stream()
				.anyMatch(section -> section.isSameWithDownStation(entry.getKey())))
			.findFirst()
			.orElseThrow(() -> new BusinessException(INVALID_STATUS))
			.getKey();
	}

	private Set<Map.Entry<Long, Long>> getCountPerStation(List<Station> stations) {
		return stations.stream()
			.collect(groupingBy(Station::getId, counting()))
			.entrySet();
	}

	private Section findBottomSection(long bottomStationId) {
		return this.values
			.stream()
			.filter(section -> section.isSameWithDownStation(bottomStationId))
			.findFirst()
			.orElseThrow(() -> new BusinessException(INVALID_STATUS));
	}

	private Section findTopSection(long topStationId) {
		return this.values
			.stream()
			.filter(section -> section.isSameWithUpStation(topStationId))
			.findFirst()
			.orElseThrow(() -> new BusinessException(INVALID_STATUS));
	}

	private List<Station> getStationByOrder(List<Section> sectionsByOrder) {
		List<Station> stations = new ArrayList<>();
		int indexOfSections = 1;
		for (Section section : sectionsByOrder) {
			stations.addAll(section.getStation(indexOfSections++));
		}
		return Collections.unmodifiableList(stations);

	}

	private Section findNextSection(Section previousSection) {
		return this.values
			.stream()
			.filter(section -> section.isSameWithUpStation(previousSection.getDownStation()))
			.findFirst()
			.orElse(previousSection);
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

		Section lastSection = findBottomSection(findFirstBottomDownStationId(findAllStations()));
		if (!lastSection.isSameWithDownStation(station)) {
			throw new BusinessException(INVALID_STATUS);
		}

		values.removeIf(section -> section.equals(lastSection));
	}

}

