package nextstep.subway.utils;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class LineHelper {
  public static Line createLine(String name, String color,
      Station up, Station down, int distance, Long id) {
    Line line = new Line(name, color, up, down, distance);
    ReflectionTestUtils.setField(line, "id", id);
    return line;
  }

}
