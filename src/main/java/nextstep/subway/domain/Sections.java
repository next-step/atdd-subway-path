package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
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
    if (getSections().isEmpty()) {
      return Collections.emptyList();
    }

    if (isOneSectionSize()) {
      return Arrays.asList(getFirstSection().getUpStation(), getFirstSection().getDownStation());
    }

    List<Station> result = new ArrayList<>();
    for (Section section : sections) {
      if (result.isEmpty()) {
        result.add(section.getUpStation());
        result.add(section.getDownStation());
        continue;
      }

      if (result.contains(section.getUpStation())) {
        int index = result.indexOf(section.getUpStation());
        result.add(index + 1, section.getDownStation());
        continue;
      }

      if (result.contains(section.getDownStation())) {
        int index = result.indexOf(section.getDownStation());
        index = index == 0 ? 0 : index - 1;
        result.add(index, section.getUpStation());
        continue;
      }
    }

    return result;
  }

  public void addSection(Section section) {
    for (Section section1 : sections) {
      if (section1.getUpStation().equals(section.getUpStation())) {

        if (section1.getDownStation().equals(section.getDownStation())) {
          throw new CustomException(HttpStatus.CONFLICT, ErrorMessage.SECTION_DUPLICATION);
        }

        if (section1.getDistance() <= section.getDistance()) {
          throw new CustomException(HttpStatus.CONFLICT, ErrorMessage.SECTION_DISTANCE_EQUALS_OR_LARGE);
        }

        Station second = section.getDownStation();
        Station third = section1.getDownStation();

        section1.changeDownStation(second);
        section.changeUpStation(second);
        section.changeDownStation(third);
        section1.changeDistance(section1.getDistance() - section.getDistance());
      }
    }

    List<Station> stations = getAllStation();
    if (!stations.isEmpty() && !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
      throw new CustomException(HttpStatus.CONFLICT, ErrorMessage.SECTION_NOT_IN_STATION);
    }

    this.sections.add(section);
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