package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorCode;

@Embeddable
public class Sections {

	private static final int MIN_SECTION_SIZE = 1;
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sectionList = new ArrayList<>();

	protected Sections() {

	}

	private Sections(List<Section> sectionList) {
		this.sectionList = sectionList;
	}

	public static Sections from(List<Section> sectionList) {
		return new Sections(sectionList);
	}

	public List<Section> getSectionList() {
		return this.sectionList;
	}

	public void addSection(Section newSection) {
		if (sectionList.isEmpty()) {
			sectionList.add(newSection);
			return;
		}

		validateExisingStation(newSection);

		Optional<Section> sameUpStationSection = getSameUpStationSection(newSection);

		if (sameUpStationSection.isPresent()) {
			validateSectionDistance(newSection, sameUpStationSection.get());
			addSectionUpStationBased(newSection, sameUpStationSection.get());
			return;
		}

		Optional<Section> sameDownStationSection = getSameDownStationSection(newSection);

		if (sameDownStationSection.isPresent()) {
			validateSectionDistance(newSection, sameDownStationSection.get());
			addSectionDownStationBased(newSection, sameDownStationSection.get());
			return;
		}

		if (isSameNewSectionDownStationAndUpStation(newSection)) {
			sectionList.add(newSection);
			return;
		}

		if (isSameNewSectionUpStationAndDownStation(newSection)) {
			sectionList.add(newSection);
			return;
		}

		throw new IllegalArgumentException("구간추가가 불가합니다.");
	}

	private void validateExisingStation(Section newSection) {
		if (findSameUpAndDownStation(newSection)) {
			throw new IllegalArgumentException("상행역과 하행역이 모두 등록되어있습니다.");
		}
	}

	public Optional<Section> getSameUpStationSection(Section newSection) {
		return this.sectionList.stream()
			.filter(section -> section.isSameUpStation(newSection))
			.findFirst();
	}

	private void validateSectionDistance(Section newSection, Section section) {
		if (newSection.isLongerDistance(section)) {
			throw new IllegalArgumentException("추가하려는 구간의 길이는 기존 구간의 길이와 같거나 길수 없습니다.");
		}
	}

	private Optional<Section> getSameDownStationSection(Section newSection) {
		return this.sectionList.stream()
			.filter(section -> section.isSameDownStation(newSection))
			.findFirst();
	}

	private void addSectionDownStationBased(Section newSection, Section section) {
		sectionList.remove(section);
		sectionList.add(new Section(section.getLine(), section.getUpStation(), newSection.getUpStation(),
			section.getDistance() - newSection.getDistance()));
		sectionList.add(new Section(section.getLine(), newSection.getUpStation(), newSection.getDownStation(),
			newSection.getDistance()));
	}

	private void addSectionUpStationBased(Section newSection, Section section) {
		sectionList.remove(section);
		sectionList.add(new Section(section.getLine(), section.getUpStation(), newSection.getDownStation(),
			newSection.getDistance()));
		sectionList.add(new Section(section.getLine(), newSection.getDownStation(), section.getDownStation(),
			section.getDistance() - newSection.getDistance()));
	}

	public boolean isSameNewSectionDownStationAndUpStation(Section newSection) {
		Section firstSection = getFirstSection();
		return firstSection.getUpStation().equals(newSection.getDownStation());
	}

	public boolean isSameNewSectionUpStationAndDownStation(Section newSection) {
		Section lastSection = getLastSection();
		return lastSection.getDownStation().equals(newSection.getUpStation());
	}

	public List<Station> getStations() {
		if (sectionList.isEmpty()) {
			return Collections.emptyList();
		}

		List<Station> sortedStationList = new ArrayList<>();
		Section section = getFirstSection();
		Section lastSection = getLastSection();

		sortedStationList.add(section.getUpStation());

		while (!section.equals(lastSection)) {
			sortedStationList.add(section.getDownStation());
			section = nextSection(section);
		}

		sortedStationList.add(lastSection.getDownStation());
		return sortedStationList;
	}

	public Section getFirstSection() {
		return this.sectionList.stream()
			.filter(section -> findSameDownStationSection(section.getUpStation()))
			.findAny()
			.orElseThrow();
	}

	private boolean findSameDownStationSection(Station upStation) {
		return this.sectionList.stream()
			.noneMatch(section -> section.getDownStation().equals(upStation));
	}

	public Section getLastSection() {
		return this.sectionList.stream()
			.filter(section -> findSameUpStationSection(section.getDownStation()))
			.findAny()
			.orElseThrow();
	}

	private boolean findSameUpStationSection(Station downStation) {
		return this.sectionList.stream()
			.noneMatch(section -> section.getUpStation().equals(downStation));
	}

	private Section nextSection(Section currentSection) {
		return this.sectionList.stream()
			.filter(section -> currentSection.getDownStation().equals(section.getUpStation()))
			.findAny()
			.orElseThrow();
	}

	public void remove(Section section) {
		this.sectionList.remove(section);
	}

	public void removeAll(List<Section> sectionList) {
		this.sectionList.removeAll(sectionList);
	}

	private boolean findSameUpAndDownStation(Section newSection) {
		return this.sectionList.stream().anyMatch(section -> section.isSameUpStation(newSection)) &&
			this.sectionList.stream().anyMatch(section -> section.isSameDownStation(newSection));
	}

	public void deleteSection(Line line, Station station) {
		validateSectionSize(line);

		// 상행 종점일 경우
		if (isFirstUpStation(station)) {
			remove(getFirstSection());
			return;
		}

		// 하행 종점일 경우
		if (isLastDownStation(station)) {
			remove(getLastSection());
			return;
		}

		// 중간역일 경우
		removeMiddleSection(line, station);
	}

	private void validateSectionSize(Line line) {
		if (isLineHasOnlyOneSection(line)) {
			throw new BusinessException(ErrorCode.COULD_NOT_DELETE_SECTION);
		}
	}

	private boolean isLineHasOnlyOneSection(Line line) {
		return line.getSections().size() <= MIN_SECTION_SIZE;
	}

	private void removeMiddleSection(Line line, Station station) {
		Sections stationContainsSection = getStationContainsSection(station);
		Station containsSectionUpStation = stationContainsSection.getContainsSectionUpStation(station);
		Station containsSectionDownStation = stationContainsSection.getContainsSectionDownStation(station);

		int distanceSum = stationContainsSection.getDistanceSum();

		this.sectionList.removeAll(stationContainsSection.getSectionList());
		this.sectionList.add(new Section(line, containsSectionUpStation, containsSectionDownStation, distanceSum));
	}

	private boolean isFirstUpStation(Station station) {
		return getFirstSection().isSameUpStation(station);
	}

	private boolean isLastDownStation(Station station) {
		return getLastSection().isSameDownStation(station);
	}

	private Sections getStationContainsSection(Station station) {
		return from(this.sectionList.stream()
			.filter(section -> section.isSameUpStation(station) || section.isSameDownStation(station))
			.collect(Collectors.toList()));
	}

	private Station getContainsSectionUpStation(Station station) {
		return this.sectionList.stream()
			.filter(section -> section.isSameDownStation(station))
			.findFirst()
			.orElseThrow(EntityNotFoundException::new)
			.getUpStation();
	}

	private Station getContainsSectionDownStation(Station station) {
		return this.sectionList.stream()
			.filter(section -> section.isSameUpStation(station))
			.findFirst()
			.orElseThrow(EntityNotFoundException::new)
			.getDownStation();
	}

	private int getDistanceSum() {
		return this.sectionList.stream()
			.mapToInt(section -> section.getDistance())
			.sum();
	}

}
