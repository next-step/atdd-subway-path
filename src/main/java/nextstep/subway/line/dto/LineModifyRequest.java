package nextstep.subway.line.dto;


public class LineModifyRequest {
  private String name;

  private String color;

  public LineModifyRequest() {
  }

  public LineModifyRequest(String name, String color) {
    this.name = name;
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }
}
