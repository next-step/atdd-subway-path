package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
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

  public List<Station> getStations() {
    Station startStation = sections.stream()
        .filter(Section::isStartStation)
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

  public void addSection(Section section) {
    // 시작 역인 경우, 시작점을 표시하기 위해  (상행역, 상행역) 인 구간을 하나 더 추가
    if (CollectionUtils.isEmpty(sections)) {
      sections.add(section.convertToStartSection());
    }
    sections.add(section);
  }

  public void removeSectionDownStationOf(Station station) {
    if (CollectionUtils.isEmpty(sections)) {
      throw new IllegalStateException();
    }

    // 하행 종점역만 제거 할 수 있음
    int lastIdx = sections.size() - 1;
    Section lastSection = sections.get(lastIdx);
    if (lastSection.getDownStation().equals(station)) {
      sections.remove(lastIdx);
    }
  }
}
