package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.Getter;

@Embeddable
public class Sections {

  public static final int MINIMUM_SIZE = 1;
  public static final int MINIMUM_DISTANCE = 0;
  @Getter
  @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private final List<Section> sections = new ArrayList<>();

  public Sections() {
  }

  private static void compareDistance(Section section, Section registeredSection) {
    if (registeredSection.getDistance() - section.getDistance() <= MINIMUM_DISTANCE) {
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
        .findFirst();
    Optional<Section> downMatch = sections.stream()
        .filter(registeredSection -> registeredSection.containSameDownStation(section))
        .findFirst();

    upMatch.ifPresent(upSection -> addUpSection(section, line, upSection));
    downMatch.ifPresent(downSection -> addDownSection(section, line, downSection));

    sections.add(section);
  }

  private void addDownSection(Section section, Line line, Section downMatch) {
    compareDistance(section, downMatch);
    sections.add(
        Section.of(line, downMatch.getDistance() - section.getDistance(),
            downMatch.getUpStation(), section.getUpStation()));
    sections.remove(downMatch);
  }

  private void addUpSection(Section section, Line line, Section upMatch) {
    compareDistance(section, upMatch);
    sections.add(
        Section.of(line, upMatch.getDistance() - section.getDistance(),
            section.getDownStation(), upMatch.getDownStation()));
    sections.remove(upMatch);
  }

  private void alreadyRegisteredValidate(Section registeredSection) {
    sections.stream().filter(section -> !section.containsSameStations(registeredSection))
        .findFirst().orElseThrow(() -> new IllegalArgumentException("동일한 Section이 존재합니다."));
  }

  private void notRegisteredValidate(Section registeredSection) {
    if (!getStations().contains(registeredSection.getUpStation()) &&
        !getStations().contains(registeredSection.getDownStation())) {
      throw new IllegalArgumentException("등록된 Station이 해당 노선에 존재하지 않습니다.");
    }
  }

  public void deleteSection(Station targetStation) {
    sectionMinimumSizeValidate();
    notRegisteredStationValidate(targetStation);

    if (getLastSection().containSameDownStation(targetStation)) {
      sections.remove(getLastSection());
      return;
    }

    Section upSection = getUpStationSameSection(targetStation);
    Section downSection = getDownStationSameSection(targetStation);

    sections.remove(upSection);
    sections.remove(downSection);

    sections.add(
        Section.of(upSection.getLine(), upSection.getDistance() + downSection.getDistance(),
            downSection.getUpStation(), upSection.getDownStation()));
  }

  private Section getDownStationSameSection(Station targetStation) {
    return sections.stream()
        .filter(section -> section.containSameDownStation(targetStation)).findFirst()
        .orElseThrow(() -> new IllegalStateException("Section의 삭제에 실패 했습니다."));
  }

  private Section getUpStationSameSection(Station targetStation) {
    return sections.stream()
        .filter(section -> section.containSameUpStation(targetStation)).findFirst()
        .orElseThrow(() -> new IllegalStateException("Section의 삭제에 실패 했습니다."));
  }

  private void notRegisteredStationValidate(Station targetStation) {
    getStations().stream().filter(station -> station.equals(targetStation))
        .findAny()
        .orElseThrow(() -> new IllegalStateException("Section에 삭제할 수 있는 Station이 없습니다."));
  }

  private void sectionMinimumSizeValidate() {
    if (sections.size() <= MINIMUM_SIZE) {
      throw new IllegalStateException("Section에 삭제할 수 있는 Station이 없습니다.");
    }
  }

  public List<Station> getStations() {
    if (sections.isEmpty()) {
      return Collections.emptyList();
    }

    List<Station> stationList = new ArrayList<>();

    Section topSection = getTopSection();

    stationList.add(topSection.getUpStation());
    stationList.add(topSection.getDownStation());

    while (hasNextSection(topSection)) {
      topSection = getNextSection(topSection);
      stationList.add(topSection.getDownStation());
    }

    return stationList;
  }

  private Section getTopSection() {
    Section topSection = sections.stream().filter(section ->
            !(getDownStations().contains(section.getUpStation()))).findFirst()
        .orElseThrow(() -> new IllegalStateException("최상단의 상행역이 존재하지 않습니다. 확인이 필요합니다."));
    return topSection;
  }

  private boolean hasNextSection(Section startingSection) {
    return sections.stream().anyMatch(iteratingSection ->
        startingSection.getDownStation().equals(iteratingSection.getUpStation()));
  }

  private Section getNextSection(Section startingSection) {
    Station downStation = startingSection.getDownStation();
    return sections.stream().filter(iteratingSection -> downStation.equals(iteratingSection.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("역끼리의 연결을 확인해주세요!"));
  }

  private Section getLastSection() {
    if (sections.size() <= MINIMUM_SIZE) {
      throw new IllegalStateException("마지막 section이 존재하지 않습니다.");
    }
    return sections.get(sections.size() - 1);
  }

  private List<Station> getDownStations() {
    return sections.stream().map(Section::getDownStation)
        .collect(Collectors.toList());
  }
  public static Sections of(List<Section> addedSections){
    Sections sections = new Sections();
    Line line = new Line();

    addedSections.forEach(section -> sections.addSection(section, line));
    return sections;
  }
}
