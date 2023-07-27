package nextstep.subway.facade;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.group.SectionGroup;
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

        validateDontEquals(source, target);

        final Station start = stationService.findById(source);
        final Station finish = stationService.findById(target);

        final List<Section> sectionList = sectionService.findAll();
        final SectionGroup sectionGroup = SectionGroup.of(sectionList);

        return new PathResponse(
            sectionGroup.getPath(start, finish).stream()
                .map(StationResponse::of)
                .collect(Collectors.toList()),
            sectionGroup.getPathDistance(start, finish)
        );
    }

    private void validateDontEquals(long source, long target) {
        if (source == target) {
            throw new IllegalArgumentException(("출발지와 목적지가 같은 역일 수 없습니다."));
        }
    }
}
