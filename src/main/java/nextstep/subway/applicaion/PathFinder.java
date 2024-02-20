package nextstep.subway.applicaion;

import java.util.Collection;
import java.util.Optional;
import nextstep.subway.applicaion.vo.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

@Component
public class PathFinder {

  public Optional<Path> find(Collection<Section> sections, Station source, Station target) {
    return Optional.empty();
  }
}
