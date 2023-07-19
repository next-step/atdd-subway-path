package nextstep.subway.facade;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.group.SectionGroup;
import nextstep.subway.service.LineService;
import nextstep.subway.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.service.SectionService;
import nextstep.subway.service.request.SectionRequest;

@Service
@Transactional
public class SectionFacade {

    private final LineService lineService;
    private final SectionService sectionService;
    private final StationService stationService;

    public SectionFacade(SectionService sectionService, StationService stationService,
        LineService lineService) {

        this.sectionService = sectionService;
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public void addSection(long id, SectionRequest request) {

        sectionService.add(
            lineService.findById(id),
            stationService.findById(request.getUpStationId()),
            stationService.findById(request.getDownStationId()),
            request.getDistance()
        );
    }

    public void deleteSection(long lineId, long stationId) {

        Line line = lineService.findById(lineId);
        SectionGroup sectionGroup = line.getSections();
        sectionGroup.delete(stationId);

    }
}
