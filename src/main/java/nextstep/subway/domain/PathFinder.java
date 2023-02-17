package nextstep.subway.domain;

import java.util.List;
import nextstep.subway.domain.dto.PathDto;
import nextstep.subway.domain.exception.PathFindException;
import org.springframework.stereotype.Component;

@Component
public class PathFinder {

    private final Path path;

    public PathFinder(final Path path) {
        this.path = path;
    }

    public void init(final List<Line> lines) {
        path.init(lines);
    }

    public PathDto find(final Station source, final Station target) {
        try {
            return path.find(source, target);
        } catch (IllegalArgumentException e) {
            throw new PathFindException();
        }
    }
}
