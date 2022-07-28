package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.AlreadyRegisteredException;
import nextstep.subway.exception.CannotInsertLongerSectionException;
import nextstep.subway.exception.CannotInsertSameDistanceSectionException;
import nextstep.subway.exception.CannotRegisterWithoutRegisteredStation;
import nextstep.subway.exception.CannotRemoveLastSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

import static nextstep.subway.exception.ErrorCode.*;

@Embeddable
@Getter
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	List<Section> sections = new ArrayList<>();

	public boolean isEmpty() {
		return sections.isEmpty();
	}

	public void add(Line line, Station upStation, Station downStation, int distance) {
		validateStations(upStation, downStation);
		addSectionWithCondition(line, upStation, downStation, distance);
	}

	public List<Station> getStations() {
		if (isEmpty()) {
			return Collections.emptyList();
		}

		Section firstSection = getFirstSection();
		List<Station> stations = addStations(firstSection);

		addAllStations(firstSection, stations);
		return stations;
	}

	public void removeSection(Station station) {

		if (isNotRegistered(station)) {
			throw new IllegalArgumentException("등록되어 있지 않은 역으로 구간을 삭제할 수 없습니다.");
		}

		if (isOnlyOneSection()) {
			throw new CannotRemoveLastSectionException(CANNOT_REMOVE_LAST_SECTION.getMessage());
		}

		if (isMiddleOfSection(station)) {
			removeMiddleStation(station);
			return;
		}

		if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
			throw new IllegalArgumentException();
		}

		sections.remove(this.getSections().size() - 1);
	}

	private void addSectionWithCondition(Line line, Station upStation, Station downStation, Integer distance) {

		if (isStartWithUpStation(upStation)) {
			addSectionInMiddleStartWithUpStation(line, upStation, downStation, distance);
			return;
		}

		if (sameAsOriginDownStationAndNewDownStation(downStation)) {
			addSectionInMiddleWithDownStation(line, upStation, downStation, distance);
			return;
		}

		if (sameAsOriginUpStationAndNewDownStation(downStation)) {
			addSectionInFront(line, upStation, downStation, distance);
			return;
		}

		this.sections.add(new Section(line, upStation, downStation, distance));
	}

	private void addSectionInFront(Line line, Station upStation, Station downStation, int distance) {
		Section section = getSectionMatchingUpStation(downStation);
		this.sections.add(sections.indexOf(section), new Section(line, upStation, downStation, distance));
	}

	private void addSectionInMiddleStartWithUpStation(Line line, Station upStation, Station downStation, int distance) {
		Section section = getSectionMatchingUpStation(upStation);
		validateDistance(distance, section);
		addSectionInMiddleStartWithUpStation(line, downStation, distance, section);
	}

	private void addSectionInMiddleStartWithUpStation(Line line, Station downStation, int distance, Section section) {
		this.sections.remove(section);
		this.sections.add(new Section(line, section.getUpStation(), downStation, distance));
		this.sections.add(new Section(line, downStation, section.getDownStation(), modifyDistanceWhenAdd(section.getDistance(), distance)));
	}

	private void addSectionInMiddleWithDownStation(Line line, Station upStation, Station downStation, int distance) {
		Section section = getSectionMatchingDownStation(downStation);
		validateDistance(distance, section);
		addSectionInMiddleWithDownStation(line, upStation, distance, section);
	}

	private void addSectionInMiddleWithDownStation(Line line, Station upStation, int distance, Section section) {
		this.sections.remove(section);
		sections.add(new Section(line, section.getUpStation(), upStation, modifyDistanceWhenAdd(section.getDistance(), distance)));
		sections.add(new Section(line, upStation, section.getDownStation(), distance));
	}

	private List<Station> addStations(Section section) {
		return new ArrayList<>(Arrays.asList(section.getUpStation(), section.getDownStation()));
	}

	private void addAllStations(Section firstSection, List<Station> stations) {
		Section nextSection = firstSection;
		while (hasNext(nextSection.getDownStation())) {
			Section section = findSection(nextSection.getDownStation());
			stations.add(section.getDownStation());
			nextSection = section;
		}
	}

	private void removeMiddleStation(Station station) {
		Section downSection = getSectionMatchingDownStation(station);
		Section upSection = getSectionMatchingUpStation(station);
		int index = sections.indexOf(downSection);
		modifyMiddleSection(downSection, upSection, index);
	}

	private void modifyMiddleSection(Section downSection, Section upSection, int index) {
		sections.remove(downSection);
		sections.remove(upSection);
		sections.add(index, new Section(upSection.getLine(), downSection.getUpStation(),
				upSection.getDownStation(), modifyDistanceWhenRemove(downSection, upSection)));
	}

	private int modifyDistanceWhenRemove(Section downSection, Section upSection) {
		return upSection.getDistance() + downSection.getDistance();
	}

	private int modifyDistanceWhenAdd(Integer origin, Integer insert) {
		return origin - insert;
	}

	private void validateDistance(int distance, Section section) {
		if (section.getDistance() < distance) {
			throw new CannotInsertLongerSectionException(CANNOT_INSERT_LONGER_SECTION.getMessage());
		}

		if (section.getDistance() == distance) {
			throw new CannotInsertSameDistanceSectionException(CANNOT_INSERT_SAME_DISTANCE_SECTION.getMessage());
		}
	}

	private void validateStations(Station upStation, Station downStation) {

		if (isAlreadyRegistered(upStation, downStation)) {
			throw new AlreadyRegisteredException(CANNOT_REGISTER_ALREADY_REGISTERED_SECTION.getMessage());
		}


		if (!isEmpty() && !haveRegistered(upStation, downStation)) {
			throw new CannotRegisterWithoutRegisteredStation(CANNOT_REGISTER_WITHOUT_REGISTERED_STATIONS.getMessage());
		}
	}

	private boolean haveRegistered(Station upStation, Station downStation) {
		return hasSameUpStation(upStation) || hasSameDownStation(downStation) ||
				hasSameUpStation(downStation) || hasSameDownStation(upStation);
	}

	private boolean isNotRegistered(Station station) {
		return !(hasSameUpStation(station) || hasSameDownStation(station));
	}

	private boolean isAlreadyRegistered(Station upStation, Station downStation) {
		return hasSameUpStation(upStation) && hasSameDownStation(downStation);
	}

	private boolean hasSameDownStation(Station station) {
		return sections.stream()
				.anyMatch(s -> s.getDownStation().equals(station));
	}

	private boolean hasSameUpStation(Station station) {
		return sections.stream()
				.anyMatch(s -> s.getUpStation().equals(station));
	}

	private boolean hasNext(Station station) {
		return sections.stream()
				.map(Section::getUpStation)
				.anyMatch(s -> s.equals(station));
	}

	private Section getSectionMatchingDownStation(Station station) {
		return sections.stream()
				.filter(s -> s.getDownStation().equals(station))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

	private Section getSectionMatchingUpStation(Station station) {
		return sections.stream()
				.filter(s -> s.getUpStation().equals(station))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

	private boolean sameAsOriginUpStationAndNewDownStation(Station downStation) {
		return hasSameUpStation(downStation);
	}

	private boolean sameAsOriginDownStationAndNewDownStation(Station downStation) {
		return hasSameDownStation(downStation);
	}

	private boolean isStartWithUpStation(Station upStation) {
		return hasSameUpStation(upStation);
	}

	private boolean isMiddleOfSection(Station station) {
		return hasSameUpStation(station) && hasSameDownStation(station);
	}

	private boolean isOnlyOneSection() {
		return sections.size() == 1;
	}

	private Section getFirstSection() {
		return sections.stream()
				.filter(s -> sections.stream()
						.noneMatch(section -> section.getDownStation().equals(s.getUpStation())))
				.findFirst()
				.orElseThrow(AssertionError::new);
	}

	private Section findSection(Station station) {
		return sections.stream()
				.filter(s -> s.getUpStation().equals(station))
				.findFirst()
				.orElseThrow(AssertionError::new);
	}
}
