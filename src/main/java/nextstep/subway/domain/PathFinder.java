package nextstep.subway.domain;

import java.util.List;


public interface PathFinder {
    Path searchShortestPath(List<Section> sections);
}
