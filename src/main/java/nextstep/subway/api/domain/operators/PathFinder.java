package nextstep.subway.api.domain.operators;

import java.util.List;

import nextstep.subway.api.domain.model.entity.Section;
import nextstep.subway.api.domain.model.entity.Station;
import nextstep.subway.api.domain.model.vo.Path;

/**
 * @author : Rene Choi
 * @since : 2024/02/09
 */
public interface PathFinder {

	Path findShortestPath(Station sourceStation, Station targetStation, List<Section> sections);
}
