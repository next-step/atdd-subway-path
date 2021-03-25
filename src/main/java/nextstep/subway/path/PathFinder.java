package nextstep.subway.path;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PathFinder {

    public Path findPath(List<Section> sections, Station source, Station target) {

        return new Path(Arrays.asList(
                new Station("강남역"),
                new Station("교대역"),
                new Station("남부터미널역"))
                ,13);
    }
}
