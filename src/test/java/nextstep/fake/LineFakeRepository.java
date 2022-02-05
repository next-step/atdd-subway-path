package nextstep.fake;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LineFakeRepository implements LineRepository {

    private Map<Long, Line> lines = new ConcurrentHashMap<>();

    @Override
    public Optional<Line> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Line save(Line entity) {
        Line line = new Line(Long.valueOf(lines.size()),entity.getName(),entity.getColor());
        lines.put(Long.valueOf(lines.size()),line);
        return line;
    }

    @Override
    public List<Line> findAll() {
        return null;
    }

    public Optional<Line> findById(Long aLong) {
        return Optional.of(lines.get(aLong));
    }

    @Override
    public void deleteById(Long id) {

    }

}
