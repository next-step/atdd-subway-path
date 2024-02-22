package nextstep.subway.domain;

import java.util.Collection;
import java.util.Optional;
import nextstep.subway.domain.vo.Path;

public interface PathFinder {

  Optional<Path> find(Collection<Section> sections, Station source, Station target);
}
