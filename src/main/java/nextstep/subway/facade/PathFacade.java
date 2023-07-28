package nextstep.subway.facade;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.group.Path;
import nextstep.subway.service.SectionService;
import nextstep.subway.service.StationService;
import nextstep.subway.service.response.PathResponse;
import nextstep.subway.service.response.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathFacade {

    private final SectionService sectionService;
    private final StationService stationService;

    public PathFacade(SectionService sectionService, StationService stationService) {
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public PathResponse getPath(long source, long target) {

        final Station start = stationService.findById(source);
        final Station finish = stationService.findById(target);

        final List<Section> sectionList = sectionService.findAll();
        final Path path = new Path(sectionList, start, finish);

        return new PathResponse(
            path.getPath().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList()),
            path.getPathDistance()
        );
    }
}
