package atdd.station.service;

import atdd.station.entity.Edge;
import atdd.station.entity.Line;
import atdd.station.usecase.LineDTO;
import java.time.LocalTime;

public class LineModelMapper {
  public Line DTOToEntity(LineDTO lineDTO) {
    return Line.builder()
        .name(lineDTO.getName())
        .startLocalTime(LocalTime.parse(lineDTO.getStartTime()))
        .lastLocalTime(LocalTime.parse(lineDTO.getLastTime()))
        .timeInterval(lineDTO.getTimeInterval())
        .extra_fare(lineDTO.getExtra_fare())
        .build();
  }

  public LineDTO EntityToDTO(Line line) {
    return LineDTO.builder()
        .id(line.getId())
        .name(line.getName())
        .startTime(line.getStartLocalTime().toString())
        .lastTime(line.getLastLocalTime().toString())
        .timeInterval(line.getTimeInterval())
        .extra_fare(line.getExtra_fare())
        .build();
  }
}
