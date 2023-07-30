package nextstep.subway.unit.fake;

import nextstep.subway.line.repository.Line;
import nextstep.subway.line.repository.LineRepository;

import java.util.*;

import static nextstep.fixture.LineFixture.신분당선;
import static nextstep.fixture.LineFixture.신분당선_ID;

public class FakeLineRepository implements LineRepository {

    @Override
    public Line save(Line line) {
        return line;
    }

    @Override
    public List<Line> findAll() {
        return null;
    }

    @Override
    public Optional<Line> findById(Long id) {
        if (Objects.equals(id, 신분당선_ID)) {
            return Optional.of(신분당선());
        }

        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

    }
}
