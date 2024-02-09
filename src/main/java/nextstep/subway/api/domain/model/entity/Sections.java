package nextstep.subway.api.domain.model.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.SortNatural;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.common.exception.SectionDeletionNotValidException;
import nextstep.subway.common.exception.SectionInsertionNotValidException;
import nextstep.subway.common.exception.SectionNotFoundException;

/**
 * Sections 일급 컬렉션으로 리팩토링하여 Line 엔티티와의 관계를 관리합니다.
 * Sections 내에서 Section 엔티티들의 생명주기를 관리하며,
 * Line 엔티티와의 연관관계를 효율적으로 관리하기 위해 사용됩니다.
 *
 * @author : Rene Choi
 * @since : 2024/02/02
 */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sections implements Iterable<Section> {

	/**
	 * 현실 세계의 도메인 컨텍스트를 반영할 때 지하철 노선도에서 '라인' 부분이 중복될 수는 없음을 고려하여
	 * Set 자료구조로 선택.
	 * 또한 향후 정렬 요구 사항의 필요성 대응을 고려하여 SortedSet으로 선택.
	 * <p>
	 * Cascade Option 적용 이슈에 대해서
	 * - Line과 Section 사이 -> 지하철 노선이 없어진다면, 그 노선에 속한 구건(Section)도 더 이상 존재할 이유가 없으므로 이 경우 CascadeType.ALL은 적절한 옵션이다.
	 * - Line과 Station 사이 -> 노선이 삭제된다고 하더라도 연관된 지하철 역이 사라져서는 안된다. 따라서 Section 와 Station 사이에서는 Cascade 옵션을 적용하면 안 된다.
	 * => 결론적으로 현재 필드에 CascadeType.ALL 옵션 적용은 불필요하다.
	 */
	// @OneToMany(fetch = FetchType.LAZY)
	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
	@JoinColumn(name = "line_id")
	@SortNatural
	private SortedSet<Section> sections = new TreeSet<>();

	@Override
	public Iterator<Section> iterator() {
		return sections.iterator();
	}

	public List<Station> parseStations() {
		return this.sections.stream()
			.flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
			.distinct()
			.collect(Collectors.toList());
	}

	public List<Section> parseSections() {
		return new ArrayList<>(this.sections);
	}


	public void addSection(Section section) {
		this.sections.add(section);
	}

	public void insertSection(Section newSection) {
		validateInsertion(newSection);

		if (!tryInsertSection(newSection)) {
			throw new SectionInsertionNotValidException("연결할 수 있는 구간이 없습니다.");
		}
	}

	/**
	 * 해당하는 stationId가
	 * 1) sections의 최상단 section의 상행역일 경우 -> 해당 Section을 삭제
	 * 2) secions의 최하단 section의 하행역일 경우 -> 해당 Section을 삭제
	 * 3) 나머지 경우 -> 중간 케이스 이므로 해당 역이 속한 Section에 대해 합쳐주어야 함
	 * A-B, B-C, C-D, D-E
	 * B인 경우 -> A-B, B-C -> 둘 다 삭제 -> A-C
	 * C인 경우 -> B-C, C-D -> 둘 다 삭제 -> B-D
	 *
	 * @param stationId
	 */
	public void removeStation(Long stationId) {
		if (isUpEndStation(stationId) || isDownEndStation(stationId)) {
			sections.remove(findSectionByStationId(stationId));
			return;
		}

		mergeSections(stationId);
	}

	public boolean isContainsAnyStation(Long stationId) {
		return this.sections.stream().anyMatch(section -> section.isAnyStation(stationId));
	}

	public boolean isUpEndStation(Long stationId) {
		return firstSection().isUpEndStation(stationId);
	}

	public boolean isDownEndStation(Long stationId) {
		return lastSection().isDownEndStation(stationId);
	}

	public boolean isSizeBelow(long size) {
		return this.sections.size() <= size;
	}

	public void removeLastSection() {
		this.sections.remove(lastSection());
	}

	private Section firstSection() {
		return this.sections.first();
	}

	private Section lastSection() {
		return this.sections.last();
	}

	private void validateInsertion(Section newSection) {
		sections.forEach(existingSection -> existingSection.validateInsertion(newSection));
	}

	private boolean tryInsertSection(Section newSection) {
		return insertAtBeginning(newSection) || insertAtEnd(newSection) || insertInMiddle(newSection);
	}

	private boolean insertAtBeginning(Section newSection) {
		return getFirstSection()
			.filter(firstSection -> firstSection.isUpStationMatches(newSection.getDownStation()))
			.map(firstSection -> {
				sections.add(newSection);
				return true;
			})
			.orElse(false);
	}

	private boolean insertAtEnd(Section newSection) {
		return getLastSection()
			.filter(lastSection -> lastSection.isDownStationMatches(newSection.getUpStation()))
			.map(firstSection -> {
				sections.add(newSection);
				return true;
			})
			.orElse(false);
	}

	/**
	 * Stream으로 전부 구현시 컬렉션을 순회하면서 컬렉션을 수정하는데, 이때 ConcurrentModificationException 예외 발생
	 * Java의 컬렉션 프레임워크는 구조적 변경이 발생할 때 fail-fast 동작을 하도록 설계되어 있어, 순회 중인 컬렉션에 대한 수정이 감지되면 이 예외가 발생.
	 * 이 문제를 해결하기 위해, 순회 전에 필터링을 완료하여 일치하는 섹션들의 목록을 먼저 수집하고 이후 필요한 변경에 대한 연산을 수행 하도록 아래와 같이 변경하였다.
	 *
	 * @param newSection
	 * @return
	 */
	private boolean insertInMiddle(Section newSection) {
		List<Section> matchingSections = sections.stream()
			.filter(section -> section.canInsertBetween(newSection))
			.collect(Collectors.toList());

		if (matchingSections.isEmpty()) {
			return false;
		}

		matchingSections.forEach(section -> processMatchingSection(section, newSection));
		return true;
	}

	private void processMatchingSection(Section existingSection, Section newSection) {
		if (existingSection.isSameUpStation(newSection.getUpStation())) {
			insertSectionAdjustUpMatch(existingSection, newSection);
		} else {
			insertSectionAdjustDownMatch(existingSection, newSection);
		}
	}

	private void insertSectionAdjustUpMatch(Section existingSection, Section newSection) {
		Section adjustedSection = existingSection.adjustForUpMatch(newSection);
		adjustSectionWithRemovalAndAdd(existingSection, newSection, adjustedSection);
	}

	private void insertSectionAdjustDownMatch(Section existingSection, Section newSection) {
		Section adjustedSection = existingSection.adjustForDownMatch(newSection);
		adjustSectionWithRemovalAndAdd(existingSection, newSection, adjustedSection);
	}

	private void adjustSectionWithRemovalAndAdd(Section existingSection, Section newSection, Section adjustedSection) {
		sections.remove(existingSection);
		sections.add(newSection);
		sections.add(adjustedSection);
	}

	private void mergeSections(Long stationId) {
		List<Section> sections = findSectionsByStationId(stationId);
		if (sections.size() != 2) {
			throw new SectionDeletionNotValidException("제거하려는 역이 올바르지 않습니다.");
		}

		Section firstSection = sections.get(0);
		Section secondSection = sections.get(1);

		Station newUpStation = firstSection.getUpStation();
		Station newDownStation = secondSection.getDownStation();

		this.sections.remove(firstSection);
		this.sections.remove(secondSection);
		addSection(createSection(newUpStation, newDownStation, calculateDeletionDistance(firstSection, secondSection)));
	}

	private Section createSection(Station newUpStation, Station newDownStation, Long newDistance) {
		return Section.builder().upStation(newUpStation).downStation(newDownStation).distance(newDistance).build();
	}

	private long calculateDeletionDistance(Section section1, Section section2) {
		return section1.getDistance() + section2.getDistance();
	}

	/**
	 * Sections가 SortedSet으로 정렬이 보장되므로 결과 시트림은 컬렉션 정렬 순서를 상속받아 정렬 순서를 보존한다.
	 * 따라서 해당 메서드는 정렬된 결과를 리턴한다.
	 */
	private List<Section> findSectionsByStationId(Long stationId) {
		return this.sections.stream().filter(section -> section.isAnyStation(stationId)).collect(Collectors.toList());
	}

	private Section findSectionByStationId(Long stationId) {
		return this.sections.stream().filter(section -> section.isAnyStation(stationId)).findAny().orElseThrow(SectionNotFoundException::new);
	}

	private Optional<Section> getLastSection() {
		return sections.isEmpty() ? Optional.empty() : Optional.of(sections.last());
	}

	private Optional<Section> getFirstSection() {
		return sections.isEmpty() ? Optional.empty() : Optional.of(sections.first());
	}
}
