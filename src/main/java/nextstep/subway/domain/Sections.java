package nextstep.subway.domain;

import static java.util.stream.Collectors.*;
import static nextstep.subway.common.exception.errorcode.BusinessErrorCode.*;
import static nextstep.subway.domain.KindOfAddition.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

import org.springframework.util.CollectionUtils;

import nextstep.subway.common.exception.BusinessException;

public class Sections {
	@OneToMany(mappedBy = "line",
		cascade = {CascadeType.PERSIST, CascadeType.MERGE},
		orphanRemoval = true)
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

		Section topSection = findFirstTopUpStationId(findAllStations());
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

	private Section findFirstTopUpStationId(List<Station> stations) {
		List<Long> stationIdCountingOne = getCountingOneStation(stations);

		return this.values
			.stream()
			.filter(section -> section.hasSameUpStation(stationIdCountingOne))
			.findFirst()
			.orElseThrow(() -> new BusinessException(INVALID_STATUS));
	}

	private Section findFirstBottomDownStationId(List<Station> stations) {
		List<Long> stationIdCountingOne = getCountingOneStation(stations);

		return this.values
			.stream()
			.filter(section -> section.hasSameDownStation(stationIdCountingOne))
			.findFirst()
			.orElseThrow(() -> new BusinessException(INVALID_STATUS));

	}

	private List<Long> getCountingOneStation(List<Station> stations) {
		return getCountPerStation(stations)
			.stream()
			.filter(key -> key.getValue() == 1)
			.map(Map.Entry::getKey)
			.collect(toList());
	}

	private Set<Map.Entry<Long, Long>> getCountPerStation(List<Station> stations) {
		return stations.stream()
			.collect(groupingBy(Station::getId, counting()))
			.entrySet();
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
			AddingSectionInExistingStations(addSection);
		}

	}

	private void AddingSectionInExistingStations(Section addSection) {
		Section baseSection = this.values
			.stream()
			.filter(section -> section.isSameWithUpStation(addSection.getUpStation()))
			.findFirst()
			.orElseThrow(() -> new BusinessException(INVALID_STATUS));

		baseSection.validateOfDistance(addSection.getDistance());
		this.values.add(this.values.indexOf(baseSection), addSection);
		baseSection.changeUpStation(addSection.getDownStation());
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

	public void remove(Station station) {

		Section lastSection = findFirstBottomDownStationId(findAllStations());
		if (!lastSection.isSameWithDownStation(station)) {
			throw new BusinessException(INVALID_STATUS);
		}

		values.removeIf(section -> section.equals(lastSection));
	}

}

