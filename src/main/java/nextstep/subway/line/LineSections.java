package nextstep.subway.line;

import nextstep.subway.Station;
import nextstep.subway.exceptions.errors.SubwayErrorCode;
import nextstep.subway.exceptions.errors.SubwayException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineSections {

  @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
  private List<LineSection> sections = new ArrayList<>();

  public List<LineSection> getSections() {
    return sections;
  }

  public void addSection(LineSection lineSection) {
    if(sections.isEmpty()) {
      sections.add(lineSection);
      return;
    }

    //기준 역이 상행역
    LineSection upSection = sections.stream()
            .filter(section -> section.containStation(lineSection.getUpStation()))
            .findFirst().orElse(null);
    //기준 역이 하행역
    LineSection downSection = sections.stream()
            .filter(section -> section.containStation(lineSection.getDownStation()))
            .findFirst().orElse(null);

    if (upSection == null && downSection == null) {
      throw new SubwayException(SubwayErrorCode.BAD_REQUEST);
    }

    if (upSection != null&& downSection != null) {
      throw new SubwayException(SubwayErrorCode.BAD_REQUEST);
    }

    if (upSection != null) {
      sections.add(upSection.getId().intValue(), lineSection);
      upSection.decreaseDistance(lineSection.getDistance());

      Station tempDownStation = lineSection.getDownStation();
      lineSection.changeDownStation(upSection.getDownStation());
      upSection.changeDownStation(tempDownStation);
      lineSection.changeUpStation(upSection.getDownStation());

      return;
    }

    if (downSection != null) {
      sections.add(downSection.getId().intValue(), lineSection);
      downSection.decreaseDistance(lineSection.getDistance());
      downSection.changeDownStation(lineSection.getUpStation());
    }
  }
}
