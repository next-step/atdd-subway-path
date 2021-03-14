package nextstep.repository;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;

import java.util.*;

public class MemoryLineRepository implements LineRepository {
    private Map<Long, Line> lines = new HashMap<>();

    @Override
    public Boolean existsByName(String name) {
        return lines.values()
                .stream()
                .anyMatch(line -> line.getName().equals(name));
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
    public Line save(Line entity) {
        boolean isNew = Objects.isNull(entity.getId());
        if (!isNew) {
            return merge(entity);
        }

        checkDuplicateName(entity);

        long id = lines.size()+1;
        Line newLine = new Line(id, entity.getName(), entity.getName(), entity.getSections());
        lines.put(id, newLine);
        return newLine;
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(line -> lines.remove(id, line));
    }

    protected void checkDuplicateName(Line entity) {
        lines.values().stream()
                .filter(line->line.getName().equals(entity.getName()))
                .findFirst()
                .ifPresent(line->{throw new RuntimeException();});
    }

    private Line merge(Line entity) {
        Line line = findById(entity.getId()).orElseThrow(IllegalArgumentException::new);
        line.update(entity);
        return line;
    }

}
