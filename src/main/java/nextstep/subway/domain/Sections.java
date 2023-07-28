package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

  @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private final List<Section> sections = new ArrayList<>();

  private static void compareDistance(Section section, Section registeredSection) {
    if (registeredSection.getDistance() - section.getDistance() <= 0) {
      throw new IllegalArgumentException("추가하려고하는 구간의 거리가 추가되는 구간의 거리보다 큽니다!!");
    }
  }

  public void addSection(Section section, Line line) {
    if (sections.isEmpty()) {
      sections.add(section);
      return;
    }

    alreadyRegisteredValidate(section);
    notRegisteredValidate(section);

    Optional<Section> upMatch = sections.stream()
        .filter(registeredSection -> registeredSection.containSameUpStation(section))
        .findAny();
    Optional<Section> downMatch = sections.stream()
        .filter(registeredSection -> registeredSection.containSameDownStation(section))
        .findFirst();

    if (upMatch.isPresent()) {
      Section registeredSection = upMatch.get();
      compareDistance(section, registeredSection);
      sections.add(
          Section.of(line, registeredSection.getDistance() - section.getDistance(),
              section.getDownStation(), registeredSection.getDownStation()));
      sections.remove(registeredSection);
    }
    if (downMatch.isPresent()) {
      Section registeredSection = downMatch.get();
      compareDistance(section, registeredSection);
      sections.add(
          Section.of(line, registeredSection.getDistance() - section.getDistance(),
              registeredSection.getUpStation(), section.getUpStation()));
      sections.remove(registeredSection);
    }
    sections.add(section);
  }

  private void alreadyRegisteredValidate(Section registeredSection) {
    sections.stream().filter(section -> !section.containsSameStations(registeredSection))
        .findFirst().orElseThrow(() -> new IllegalArgumentException("동일한 Section이 존재합니다."));
  }

  private void notRegisteredValidate(Section registeredSection) {
    if (!getStations().contains(registeredSection.getUpStation()) && !getStations().contains(
        registeredSection.getDownStation())) {
      throw new IllegalArgumentException("등록된 Station이 해당 노선에 존재하지 않습니다.");
    }
  }

  public void deleteSection(Station downStation) {
    if (sections.stream().count() <= 1) {
      throw new IllegalStateException("Section에 삭제할 수 있는 Station이 없습니다.");
    }
    Section lastSection = getLastSection();
    if (!lastSection.getDownStation().equals(downStation)) {
      throw new IllegalStateException("Section에 삭제할 수 있는 Station이 없습니다.");
    }
    getStations().stream().filter(station -> station.equals(downStation)).findAny()
        .ifPresent(x -> new IllegalStateException("Section에 삭제할 수 있는 Station이 없습니다."));
    sections.remove(sections.size() - 1);
  }

  public List<Station> getStations() {
    if (sections.isEmpty()) {
      return Collections.emptyList();
    }
    List<Station> stationList = new ArrayList<>();
    List<Station> getDownStations = new ArrayList<>();
    Section topSection = sections.stream().filter(section ->
            !(getDownStations().contains(section.getUpStation()))).findFirst()
        .orElseThrow(() -> new IllegalStateException("최상단의 상행역이 존재하지 않습니다. 확인이 필요합니다."));

    stationList.add(topSection.getUpStation());
    stationList.add(topSection.getDownStation());

    while (hasNextSection(topSection)) {
      topSection = getNextSection(topSection);
      stationList.add(topSection.getDownStation());
    }

    return stationList;
  }

  public boolean hasNextSection(Section startingSection) {
    return sections.stream().anyMatch(iteratingSection ->
        startingSection.getDownStation().equals(iteratingSection.getUpStation()));
  }

  public Section getNextSection(Section startingSection) {
    return sections.stream().filter(iteratingSection ->
            startingSection.getDownStation().equals(iteratingSection.getUpStation())).findFirst()
        .orElseThrow(() -> new IllegalStateException("역끼리의 연결을 확인해주세요!"));
  }

  public Section getLastSection() {
    if (sections.size() <= 1) {
      throw new IllegalStateException("마지막 section이 존재하지 않습니다.");
    }
    return sections.get(sections.size() - 1);
  }

  private List<Station> getDownStations() {
    return sections.stream().map(Section::getDownStation).collect(
        Collectors.toList());
  }

}
