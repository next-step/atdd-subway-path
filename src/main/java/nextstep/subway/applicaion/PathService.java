package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.DijkstraUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final StationService stationService;
    private final SectionService sectionService;

    public PathService(StationService stationService, SectionService sectionService) {
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public PathResponse searchPath(Long sourceStationId, Long targetStationId) {

        Station sourceStation = stationService.findById(sourceStationId);
        Station targetStation = stationService.findById(targetStationId);

        List<Section> allSections = sectionService.findAllSections();

        List<StationResponse> stationResponses = DijkstraUtils.searchPath(allSections, sourceStation, targetStation)
                .stream().map(StationResponse::from)
                .collect(Collectors.toList());
        return new PathResponse(stationResponses);
    }
}
