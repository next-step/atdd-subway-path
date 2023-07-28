package nextstep.subway.unit.fixture;

import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.List;

public class PathFixture {

    public static Path 지하철_최단_경로_조회(List<Section> sections, Station source, Station target){
        return new PathFinder(sections).getShortestPath(source,target);
    }
}
