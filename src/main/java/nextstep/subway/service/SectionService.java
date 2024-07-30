package nextstep.subway.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.SectionBuilder;
import org.springframework.stereotype.Service;
import nextstep.subway.domain.Section;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SubwayLineService subwayLineService;
    private final StationService stationService;

    @Transactional
    public SectionResponse save(Long lineId, SectionRequest request) {
        var subwayLine = subwayLineService.findSubwayLineOrElseThrow(lineId);
        var upStation = stationService.findStationOrElseThrow(request.getUpStationId());
        var downStation = stationService.findStationOrElseThrow(request.getDownStationId());
        var section = new SectionBuilder()
                .distance(request.getDistance())
                .upStation(upStation)
                .downStation(downStation)
                .build();
        subwayLine.addSection(section);
        return new SectionResponse(section.getId());
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        var subwayLine = subwayLineService.findSubwayLineOrElseThrow(lineId);
        subwayLine.removeSection(stationId);
    }
}
