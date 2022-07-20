package nextstep.subway.fake;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeLineRepository implements LineRepository {

    private static final Map<Long, Line> lineMap = new HashMap<>();

    private static long sequence = 1L;

    @Override
    public Line save(Line entity) {
        long id = ++sequence;
        Line line = new Line(id, entity.getName(), entity.getColor(), entity.getSections().get(0));
        lineMap.put(id, line);
        return line;
    }

    @Override
    public List<Line> findAll() {
        return new ArrayList<>(lineMap.values());
    }

    @Override
    public Optional<Line> findById(Long aLong) {
        return Optional.ofNullable(lineMap.get(aLong));
    }

    @Override
    public void deleteById(Long aLong) {
        lineMap.remove(aLong);
    }
}
