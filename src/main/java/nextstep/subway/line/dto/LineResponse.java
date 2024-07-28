package nextstep.subway.line.dto;

import nextstep.subway.StationResponse;
import nextstep.subway.line.Line;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineResponse {

  private Long id;

  private String name;

  private String color;

  private List<StationResponse> stations;

  public LineResponse() {
  }

  public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
    this.id = id;
    this.name = name;
    this.color = color;
    this.stations = stations;
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

  public List<StationResponse> getStations() {
    return stations;
  }

  public static LineResponse from(Line line) {
    List<StationResponse> stations = line.getSections().stream().
      flatMap(section -> Stream.of(
        new StationResponse(section.getUpStation().getId(), section.getUpStation().getName()),
        new StationResponse(section.getDownStation().getId(), section.getDownStation().getName())
      ))
      .distinct()
      .collect(Collectors.toList());

    return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
  }
}
