package nextstep.subway.domain.line.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionExceptionCode;
import nextstep.subway.exception.StationExceptionCode;
import nextstep.subway.exception.SubwayException;
import org.springframework.util.CollectionUtils;

@Embeddable
public class Sections {

  @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
  private List<Section> sections = new ArrayList<>();

  public List<Section> getSections() {
    return sections;
  }

  public List<Station> getStationsOfSection() {
    return this.getStartSection()
        .map(startSection -> traversalAndGetStationsInOrder(startSection.getUpStation()))
        .orElse(Collections.emptyList());
  }

  private List<Station> traversalAndGetStationsInOrder(Station startStation) {
    if (startStation == null) {
      return Collections.emptyList();
    }

    Map<Long, Section> upStationMap = sections.stream()
        .filter(section -> !section.getDownStation().equals(startStation))
        .collect(Collectors.toMap(section -> section.getUpStation().getId(), Function.identity()));

    List<Station> stations = new ArrayList<>(sections.size() * 2);
    long currentUpStationId = startStation.getId();
    long prevUpStationId = startStation.getId();
    Section current;
    while ((current = upStationMap.get(currentUpStationId)) != null) {
      stations.add(current.getUpStation());
      Station downStation = current.getDownStation();

      // 이전구간 상행역 세팅
      prevUpStationId = currentUpStationId;

      // 다음 구강 상행역 세팅
      currentUpStationId = downStation.getId();
    }

    // 종점 처리
    Section lastSection = upStationMap.get(prevUpStationId);
    if (lastSection != null) {
      stations.add(lastSection.getDownStation());
    }

    return stations;
  }

  public Optional<Section> getStartSection() {
    Map<Long, Station> downStationMap = sections.stream()
        .map(Section::getDownStation)
        .collect(Collectors.toMap(Station::getId, Function.identity()));

    return sections.stream()
        .filter(section -> downStationMap.get(section.getUpStation().getId()) == null)
        .findFirst();
  }

  public Section addSection(Section newSection) {
    List<Station> stationsOfSection = this.getStationsOfSection();
    if (CollectionUtils.isEmpty(stationsOfSection)) {
      sections.add(newSection);
      return newSection;
    }

    throwIfBothStationAreAlreadyIncluded(newSection);
    throwIfBothStationAreNotIncluded(stationsOfSection, newSection);

    sections.stream()
        .filter(section -> section.isSuperSetOf(newSection))
        .findFirst()
        .ifPresentOrElse(
            superSection -> this.interposeSection(superSection, newSection),
            () -> sections.add(newSection)
        );

    return newSection;
  }

  // 구간 사이에 새로운 구간을 추가 해야 하는 경우
  private void interposeSection(Section superSetSection, Section newSection) {
    if (superSetSection.isUpStationEquals(newSection.getUpStation())) {
      superSetSection.interposeSectionAtUpStation(newSection);
    }

    if (superSetSection.isDownStationEquals(newSection.getDownStation())) {
      superSetSection.interposeSectionAtDownStation(newSection);
    }

    sections.add(newSection);
  }

  // 신규 구간이 이미 존재하는 구간인 경우 (역방향 포함)
  private void throwIfBothStationAreAlreadyIncluded(Section newSection) {
    sections.stream()
        .filter(section -> section.isSectionEquals(newSection))
        .findFirst()
        .ifPresent(section -> {
          throw new SubwayException(SectionExceptionCode.SECTION_ALREADY_EXIST);
        });
  }

  // 새로운 구간의 상,하행역 모두 노선에 있는 역이면 exception
  private void throwIfBothStationAreNotIncluded(List<Station> stations, Section newSection) {
    if (stations.contains(newSection.getUpStation()) && stations.contains(newSection.getDownStation())) {
      throw new SubwayException(StationExceptionCode.STATION_ALREADY_EXIST);
    }
  }

  public void removeSection(Line line, Station station) {
    if (CollectionUtils.isEmpty(sections)) {
      throw new SubwayException(SectionExceptionCode.CANNOT_DELETE_SECTION, "삭제 할 구간이 없습니다.");
    }

    if (sections.size() == 1) {
      throw new SubwayException(SectionExceptionCode.CANNOT_DELETE_LAST_SECTION);
    }

    List<Section> sectionsToRemove = sections.stream()
        .filter(section -> section.containsStation(station))
        .collect(Collectors.toUnmodifiableList());

    if (CollectionUtils.isEmpty(sectionsToRemove)) {
      throw new SubwayException(SectionExceptionCode.SECTION_NOT_FOUND);
    }

    sections.removeAll(sectionsToRemove);
    if (sectionsToRemove.size() == 1) {
      return;
    }

    addRebalancedSectionIfMiddleSectionDeleted(line, station, sectionsToRemove);
  }

  private void addRebalancedSectionIfMiddleSectionDeleted(Line line, Station stationToDelete, List<Section> sectionsToRemove) {
    Section upSection = sectionsToRemove.stream()
        .filter(sectionToRemove -> sectionToRemove.isDownStationEquals(stationToDelete))
        .findFirst()
        .orElseThrow(() -> new SubwayException(SectionExceptionCode.SECTION_NOT_FOUND));

    Section downSection = sectionsToRemove.stream()
        .filter(sectionToRemove -> sectionToRemove.isUpStationEquals(stationToDelete))
        .findFirst()
        .orElseThrow(() -> new SubwayException(SectionExceptionCode.SECTION_NOT_FOUND));

    int distance = upSection.getDistance() + downSection.getDistance();
    Section rebalancedSection = new Section(line, upSection.getUpStation(), downSection.getDownStation(), distance);
    sections.add(rebalancedSection);
  }

  public int getTotalDistance() {
    return sections.stream()
        .map(Section::getDistance)
        .mapToInt(Integer::intValue)
        .sum();
  }
}
