package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.SectionExceptionCode;
import nextstep.subway.exception.StationExceptionCode;
import nextstep.subway.exception.SubwayException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

@Embeddable
public class Sections {

  @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
  private List<Section> sections = new ArrayList<>();

  public List<Section> getSections() {
    return sections;
  }

  public List<Station> getStationsOfAllSection() {
    Station startStation = sections.stream()
        .filter(Section::isStartSection)
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "노선의 시작점이 없습니다."))
        .getUpStation();

    return traversalAndGetStationsInOrder(startStation);
  }

  private List<Station> traversalAndGetStationsInOrder(Station startStation) {
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

  public Section addSection(Section newSection) {

    // 시작 역인 경우, 시작점을 표시하기 위해  (상행역, 상행역) 인 구간을 하나 더 추가
    if (CollectionUtils.isEmpty(sections)) {
      return addStartStation(newSection);
    }

    List<Station> stationsOfAllSection = this.getStationsOfAllSection();
    throwIfBothStationAreAlreadyIncluded(newSection);
    throwIfBothStationAreNotIncluded(stationsOfAllSection, newSection);

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

    // 새로운 역이 상행 종점으로 등록되는 경우
    if (superSetSection.isStartSection()) {
      removeStartSection(superSetSection);
      addStartStation(newSection);
      return;
    }

    if (superSetSection.isUpStationEquals(newSection.getUpStation())) {
      superSetSection.interposeSectionAtUpStation(newSection);
    }

    if (superSetSection.isDownStationEquals(newSection.getDownStation())) {
      superSetSection.interposeSectionAtDownStation(newSection);
    }

    sections.add(newSection);
  }

  private Section addStartStation(Section newSection) {
    sections.add(newSection.convertToStartSection());
    sections.add(newSection);
    return newSection;
  }

  private void removeStartSection(Section startSection) {
    if (!startSection.isStartSection()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제하려는 구간은 노선의 시작구간이 아닙니다.");
    }

    List<Section> startSectionFilteredSections = sections.stream()
        .filter(section -> !section.isSectionEquals(startSection))
        .collect(Collectors.toList());
    sections.clear();
    sections.addAll(startSectionFilteredSections);
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

  public void removeSectionDownStationOf(Station station) {
    if (CollectionUtils.isEmpty(sections)) {
      throw new SubwayException(SectionExceptionCode.CANNOT_DELETE_SECTION, "삭제 할 구간이 없습니다.");
    }

    // 하행 종점역만 제거 할 수 있음
    int lastIdx = sections.size() - 1;
    Section lastSection = sections.get(lastIdx);
    if (lastSection.getDownStation().equals(station)) {
      sections.remove(lastIdx);
    }
  }

  public int getTotalDistance() {
    return sections.stream()
        .map(Section::getDistance)
        .mapToInt(Integer::intValue)
        .sum();
  }
}
