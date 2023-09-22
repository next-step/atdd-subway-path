package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathService {
    private SectionRepository sectionRepository;
    private StationService stationService;

    public PathService(SectionRepository sectionRepository, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public PathResponse showPath(PathRequest request) {
        PathFinder pathFinder = new PathFinder(sectionRepository.findAll());
        Station source = stationService.findById(request.getSource());
        Station target = stationService.findById(request.getTarget());
        pathFinder.setSourceAndTarget(source, target);
        return PathResponse.from(pathFinder);
    }
}
