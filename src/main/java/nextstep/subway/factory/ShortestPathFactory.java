package nextstep.subway.factory;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.ShortestPathType;
import nextstep.subway.exception.ApplicationException;
import nextstep.subway.strategy.Dijkstra;
import nextstep.subway.strategy.ShortestPathStrategy;

import java.util.List;
import java.util.Objects;

public class ShortestPathFactory {

    public ShortestPathStrategy generateStrategy(ShortestPathType shortestPathType, List<Section> sections) {
        if (sections.isEmpty()) {
            throw new ApplicationException("지하철 구간이 존재하지 않습니다.");
        }

        if (Objects.requireNonNull(shortestPathType) == ShortestPathType.DIJKSTRA) {
            return new Dijkstra(sections);
        }
        throw new ApplicationException("존재하지 않는 알고리즘 최단 전략 입니다.");

    }
}
