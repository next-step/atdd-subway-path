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
import nextstep.subway.common.exception.ErrorMessage;
import org.springframework.http.HttpStatus;

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
      throw new CustomException(HttpStatus.CONFLICT, ErrorMessage.SECTION_DUPLICATION);
    }
  }

  private void distanceCompare(Section section, Section target) {
    if (section.getDistance() <= target.getDistance()) {
      throw new CustomException(HttpStatus.CONFLICT, ErrorMessage.SECTION_DISTANCE_EQUALS_OR_LARGE);
    }
  }

  private void stationNotInSection(Section target) {
    List<Station> stations = getAllStation();
    if (!isSectionEmpty() && !stations.contains(target.getUpStation()) && !stations.contains(target.getDownStation())) {
      throw new CustomException(HttpStatus.CONFLICT, ErrorMessage.SECTION_NOT_IN_STATION);
    }
  }

  public Section getFirstSection() {
    return this.sections.get(0);
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

  public void isDeleteStationCheck(Station station) {
    if (!isLastStationEqualCheck(station)) {
      throw new IllegalArgumentException();
    }
  }

  public boolean isLastStationEqualCheck(Station station) {
    return getLastSection().getDownStation().equals(station);
  }

  public boolean isOneSectionSize() {
    return getSectionSize() == MIN_SECTION_SIZE;
  }

  public boolean isSectionEmpty() {
    return sections.isEmpty();
  }
}