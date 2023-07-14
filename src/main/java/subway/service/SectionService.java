package subway.service;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import subway.entity.Section;
import subway.dto.request.SectionRequest;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final LineService lineService;
    private final StationService stationService;

    @Transactional
    public Section addSection(Long lineId, SectionRequest request) {
        var sections = lineService.findLine(lineId).getSections();

        var upStation = stationService.findStation(request.getUpStationId());
        var downStation = stationService.findStation(request.getDownStationId());

        sections.addSection(Section.builder()
            .upStation(upStation)
            .downStation(downStation)
            .distance(request.getDistance())
            .build());

        return sections.lastSection();
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        var sections = lineService.findLine(lineId).getSections();

        sections.deleteSection(stationId);
    }
}
