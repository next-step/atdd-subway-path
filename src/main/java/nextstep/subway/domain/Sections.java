package nextstep.subway.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {

  private static final int MIN_SECTION_SIZE = 1;

  @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
  @OrderBy("id asc")
  private List<Section> sections = new ArrayList<>();

  public List<Station> getAllStation() {
    if (getSections().isEmpty()) {
      return Collections.emptyList();
    }

    if (isOneSectionSize()) {
      return Arrays.asList(getFirstSection().getUpStation(), getFirstSection().getDownStation());
    }

    return Stream.concat(
            Stream.of(getFirstSection().getUpStation()),
            getSections().stream().map(Section::getDownStation)
        )
        .collect(toList());
  }

  public void addSection(Section section) {
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