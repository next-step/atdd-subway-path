package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.exception.DoseNotExistStationOfSectionException;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.DoseNotConnectedException;
import nextstep.subway.path.exception.EqualsStationsException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.path.domain.PathFinder.getShortedPath;

@Service
public class PathService {
    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;

    public PathService(LineService lineService, StationService stationService, SectionService sectionService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public PathResponse getPath(long sourceId, long targetId) {

        if (sourceId == targetId) {
            throw new EqualsStationsException();
        }

        GraphPath<Station, DefaultWeightedEdge> graphPath = getShortedPath(
            sectionService.findAll(),
            stationService.findById(sourceId),
            stationService.findById(targetId)
        );

        if (graphPath == null) {
            throw new DoseNotConnectedException();
        }

        return PathResponse.of(
            graphPath.getVertexList(),
            (int) graphPath.getWeight()
        );

    }

}
