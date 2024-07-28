package nextstep.subway.line;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Line {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String color;

  @Embedded
  private LineSections lineSections = new LineSections();

  public Line() {
  }

  public Line(Long id, String name, String color, LineSections lineSections) {
    this.id = id;
    this.name = name;
    this.color = color;
    this.lineSections = lineSections;
  }

  public Line(String name, String color, LineSections lineSections) {
    this.name = name;
    this.color = color;
    this.lineSections = lineSections;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateColor(String color) {
    this.color = color;
  }

  public void appendSection(LineSection lineSection) {
    lineSections.addSection(lineSection);
  }

  public List<LineSection> getSections() {
    return lineSections.getSections();
  }
 }
