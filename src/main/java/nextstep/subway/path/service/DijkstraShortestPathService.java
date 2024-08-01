package nextstep.subway.path.service;

import nextstep.subway.line.entity.Line;
import nextstep.subway.path.domain.GraphModel;
import nextstep.subway.path.dto.Path;
import nextstep.subway.path.dto.PathResponse;

import java.util.List;

public class DijkstraShortestPathService implements PathService {
    @Override
    public PathResponse findPath(Long source, Long target, List<Line> lineList) {
        GraphModel graphModel = new GraphModel(source, target);
        Path path = graphModel.findPath(lineList);
        return path.createPathResponse();
    }
}
