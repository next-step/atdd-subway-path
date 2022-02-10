package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
  @Embedded
  private final Sections sections = new Sections();
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String name;
  private String color;

  public Line() {
  }

  public Line(String name, String color) {
    this.name = name;
    this.color = color;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void updateName(String name) {
    this.name = name;
  }

  public String getColor() {
    return color;
  }

  public void updateColor(String color) {
    this.color = color;
  }

  public Sections getSections() {
    return sections;
  }

  public void addSection(Section section) {
    sections.addSection(section);
  }

  public void removeSection(Station station) {
    sections.deleteSectionFromStation(station);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Line line = (Line) o;
    return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, color);
  }
}
