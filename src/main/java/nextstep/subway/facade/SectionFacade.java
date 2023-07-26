package nextstep.subway.facade;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.group.SectionGroup;
import nextstep.subway.service.LineService;
import nextstep.subway.service.SectionService;
import nextstep.subway.service.StationService;
import nextstep.subway.service.request.SectionRequest;
import nextstep.subway.service.response.PathResponse;
import nextstep.subway.service.response.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Line line = lineService.findById(id);
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        sectionService.save(line.addSection(upStation, downStation, request.getDistance()));

    }

    public void deleteSection(long lineId, long stationId) {

        lineService.deleteSectionStation(lineId, stationId);
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
