package nextstep.subway.facade;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.group.SectionGroup;
import nextstep.subway.service.LineService;
import nextstep.subway.service.SectionService;
import nextstep.subway.service.StationService;
import nextstep.subway.service.request.LineModifyRequest;
import nextstep.subway.service.request.LineRequest;
import nextstep.subway.service.response.LineResponse;
import nextstep.subway.service.response.StationResponse;

@Service
@Transactional
public class LineFacade {

    private final StationService stationService;
    private final LineService lineService;
    private final SectionService sectionService;

    public LineFacade(StationService stationService, LineService lineService,
        SectionService sectionService) {
        this.stationService = stationService;
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    public LineResponse lineCreate(LineRequest request) {

        final Line line = lineService.create(request);
        final Section section = sectionService.create(
            line,
            stationService.findById(request.getUpStationId()),
            stationService.findById(request.getDownStationId()),
            request.getDistance()
        );

        final List<StationResponse> stationResponses = stationService.findAllIn(
            section.getStationIdList());

        return LineResponse.of(line, stationResponses);
    }


    public LineResponse lineFindById(long id) {

        final Line line = lineService.findById(id);
        return getStationLineResponse(line);
    }

    private LineResponse getStationLineResponse(Line line) {

        final SectionGroup sectionGroup = SectionGroup.of(
            sectionService.findAllByLineId(line.getId())
        );

        final List<StationResponse> stationResponses = stationService.findAllIn(
            sectionGroup.getStationsId()
        );

        return LineResponse.of(line, stationResponses);
    }

    public List<LineResponse> findAllStationLines() {

        return lineService.findAllLine()
            .stream()
            .map(this::getStationLineResponse)
            .collect(Collectors.toList());

    }

    public void modifyLine(long id, LineModifyRequest request) {
        lineService.modify(id, request.getName(), request.getColor());
    }

    public void deleteLine(long id) {
        lineService.delete(id);
    }
}
