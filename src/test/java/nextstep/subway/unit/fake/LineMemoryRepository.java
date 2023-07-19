package nextstep.subway.unit.fake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.infra.LineRepository;

public class LineMemoryRepository implements LineRepository {

  private final Map<Long, Line> data = new HashMap<>();

  @Override
  public Line save(Line line) {
    Long id = (long) (data.size() + 1);
    line = new Line(id, line.getName(), line.getColor(), line.getSections());
    data.put(id, line);
    return line;
  }

  @Override
  public List<Line> findAll() {
    return (List<Line>) data.values();
  }

  @Override
  public Optional<Line> findById(Long id) {
    return Optional.ofNullable(data.get(id));
  }

  @Override
  public void deleteById(Long id) {
    data.remove(id);
  }
}
