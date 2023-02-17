package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PathFinder {
	PathResponse find(List<Line> lines, Station source, Station target);
}
