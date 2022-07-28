package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.message.SectionErrorMessage;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {

  private static final int MIN_SECTION_SIZE = 1;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
  @OrderBy("id asc")
  private List<Section> sections = new ArrayList<>();

  public List<Station> getAllStation() {
    if (isSectionEmpty()) {
      return Collections.emptyList();
    }

    List<Station> result = new ArrayList<>();
    for (Section section : sections) {
      if (result.isEmpty()) {
        result.add(section.getUpStation());
        result.add(section.getDownStation());
        continue;
      }

      if (result.contains(section.getUpStation())) {
        result.add(getStationIndex(result, section.getUpStation()) + 1, section.getDownStation());
        continue;
      }

      if (result.contains(section.getDownStation())) {
        int index = getStationIndex(result, section.getDownStation());
        index = index == 0 ? 0 : index - 1;
        result.add(index, section.getUpStation());
        continue;
      }
    }

    return result;
  }

  private int getStationIndex(List<Station> result, Station station) {
    return result.indexOf(station);
  }

  public void addSection(Section target) {
    sections.stream()
        .filter(section -> section.getUpStation().equals(target.getUpStation()))
        .forEach(section -> {
            sectionEqualsCheck(section, target);
            distanceCompare(section, target);

            Station middleStation = target.getDownStation();
            Station lastStation = section.getDownStation();

            section.changeDownStation(middleStation);
            target.changeUpStation(middleStation);
            target.changeDownStation(lastStation);
            section.changeDistance(section.getDistance() - target.getDistance());
        });

    stationNotInSection(target);

    this.sections.add(target);
  }

  private void sectionEqualsCheck(Section section, Section target) {
    if (section.getDownStation().equals(target.getDownStation())) {
      throw new CustomException(SectionErrorMessage.SECTION_DUPLICATION);
    }
  }

  private void distanceCompare(Section section, Section target) {
    if (section.getDistance() <= target.getDistance()) {
      throw new CustomException(SectionErrorMessage.SECTION_DISTANCE_EQUALS_OR_LARGE);
    }
  }

  private void stationNotInSection(Section target) {
    List<Station> stations = getAllStation();
    if (!isSectionEmpty() && !stations.contains(target.getUpStation()) && !stations.contains(target.getDownStation())) {
      throw new CustomException(SectionErrorMessage.SECTION_NOT_IN_STATION);
    }
  }

  public Section getLastSection() {
    return this.sections.get(this.sections.size() - 1);
  }

  public int getSectionSize() {
    return this.sections.size();
  }

  public void removeLastSection() {
    this.sections.remove(getLastSection());
  }

  public void isDeleteStationCheck() {
    if (isOneSectionSizeCheck()) {
      throw new CustomException(SectionErrorMessage.SECTION_ONLY_ONE);
    }
  }

  public boolean isOneSectionSizeCheck() {
    return getSectionSize() == 1;
  }

  public boolean isSectionEmpty() {
    return sections.isEmpty();
  }

  public void removeSection(Station station) {
    isDeleteStationCheck();

    List<Station> stations = getAllStation();
    if (!stations.contains(station)) {
      throw new CustomException(SectionErrorMessage.SECTION_NOT_EQUALS);
    }

    if (stations.get(0).equals(station)) {
      for(Section section : sections) {
        if (section.getUpStation().equals(station)) {
          sections.remove(section);
          System.out.println(sections.size());
          return;
        }
      }
    }

    if (stations.get(stations.size() - 1).equals(station)) {
      for(Section section : sections) {
        if (section.getDownStation().equals(station)) {
          sections.remove(section);
          return;
        }
      }
    }

    Section firstSection = new Section();
    Section secondSection = new Section();
    for (Section section : sections) {
      if (section.getDownStation().equals(station)) {
        firstSection = section;
      }

      if (section.getUpStation().equals(station)) {
        secondSection = section;
      }
    }
    System.out.println(firstSection.getDownStation());
    System.out.println(secondSection.getUpStation());
    firstSection.changeDownStation(secondSection.getDownStation());
    firstSection.changeDistance(firstSection.getDistance() + secondSection.getDistance());
    sections.remove(secondSection);
  }
}