package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathRequest;

import java.util.List;


public interface PathFinder {
    Path searchShortestPath(PathRequest request, List<Section> mergeSections);
}
