package nextstep.subway.domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import org.springframework.dao.DataIntegrityViolationException;

@Embeddable
public class Sections {

  @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
  @OrderColumn(name="section_order")
  private List<Section> sections;

  public Sections() {
    this.sections = new LinkedList<>();
  }

  public List<Section> getSections() {
    return sections;
  }

  public void remove(Section section) {
    sections.remove(section);
  }

  public List<Station> getStations() {
    if (sections.isEmpty()) {
      return Collections.emptyList();
    }
    List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
    stations.add(sections.get(sections.size() - 1).getDownStation());
    return stations;
  }

  public void addSection(Line line, Section section) {
    List<Station> stations = getStations();
    validateAddSection(stations, section);

    if (stations.isEmpty()) {
      sections.add(section);
      return;
    }

    for (int i = 0; i < sections.size(); i++) {
      Section origin = sections.get(i);
      if (Objects.equals(origin.getUpStation(), section.getUpStation())) {
        addSectionBackOfUpStation(line, origin, section, i);
      } else if (Objects.equals(origin.getDownStation(), section.getDownStation())) {
        addSectionFrontOfDownStation(line, origin, section, i);
      } else if (Objects.equals(origin.getUpStation(), section.getDownStation())) {
        addUpStation(section);
      } else if (Objects.equals(origin.getDownStation(), section.getUpStation())) {
        addDownStation(section);
      } else {
        continue;
      }
      updateOrderValues();
      return;
    }
  }

  private void addUpStation(Section section) {
    sections.add(0, section);
  }

  private void addDownStation(Section section) {
    sections.add(section);
  }

  private void addSectionBackOfUpStation(Line line, Section origin ,Section newbie, int i) {
    validateDistance(newbie, origin);

    Section right = new Section(line, newbie.getDownStation(), origin.getDownStation(), origin.getDistance() - newbie.getDistance());
    sections.set(i, newbie);
    sections.add(i + 1, right);
  }

  private void addSectionFrontOfDownStation(Line line, Section origin, Section newbie, int i) {
    validateDistance(newbie, origin);
    Section left = new Section(line, origin.getUpStation(), newbie.getUpStation(), origin.getDistance() - newbie.getDistance());
    sections.set(i, left);
    sections.add(i + 1, newbie);
  }


  private void validateAddSection(List<Station> stations, Section section) {
    if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
      throw new DataIntegrityViolationException("둘 다 등록된 역입니다.");
    }

    if (!stations.isEmpty() && !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
      throw new DataIntegrityViolationException("노선에 추가하려는 역이 모두 등록되지 않은 역입니다");
    }
  }

  private void validateDistance(Section newbie, Section origin) {
    if (newbie.getDistance() >= origin.getDistance()) {
      throw new DataIntegrityViolationException("노선에 추가하려는 중간 구간의 거리가 기존 구간보다 깁니다");
    }
  }

  private void updateOrderValues() {
    for (int i = 0; i < sections.size(); i++) {
      sections.get(i).setOrder(i);
    }
  }
}
