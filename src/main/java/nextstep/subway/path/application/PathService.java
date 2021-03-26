package nextstep.subway.path.application;

import nextstep.subway.member.domain.LoginMember;
import nextstep.subway.path.domain.PathResult;
import nextstep.subway.path.domain.PathType;
import nextstep.subway.path.domain.SubwayGraph;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class PathService {
    private GraphService graphService;
    private StationService stationService;

    public PathService(GraphService graphService, StationService stationService) {
        this.graphService = graphService;
        this.stationService = stationService;
    }

    public PathResponse findPath(LoginMember loginMember, Long source, Long target, PathType type) {
        SubwayGraph subwayGraph = graphService.findGraph(loginMember, type);
        Station sourceStation = stationService.findMyStationById(loginMember, source);
        Station targetStation = stationService.findMyStationById(loginMember, target);
        PathResult pathResult = subwayGraph.findPath(sourceStation, targetStation);
        return PathResponse.of(pathResult);
    }
}
