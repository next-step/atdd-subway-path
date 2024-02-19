package nextstep.subway.service;

import nextstep.subway.domain.Station;
import nextstep.subway.service.dto.LineDto;
import nextstep.subway.service.dto.PathDto;

import java.util.List;

public interface PathFinder {
    public PathDto findShortestPath(List<LineDto> lines, Station sourceStation, Station targetStation);
}
