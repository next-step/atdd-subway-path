package nextstep.subway.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;

public class InMemoryLineRepository implements LineRepository {

    private Map<Long, Line> lines = new HashMap<>();

    @Override
    public Line save(Line line) {
        lines.put(line.getId(), line);
        return line;
    }

    @Override
    public List<Line> findAll() {
        return new ArrayList<>(lines.values());
    }

    @Override
    public Optional<Line> findById(Long id) {
        return Optional.ofNullable(lines.get(id));
    }

    @Override
    public void deleteById(Long id) {
        lines.remove(id);
    }
}
