package subway.service;

import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import subway.dto.response.LineResponse;
import subway.entity.Section;
import subway.dto.request.SectionRequest;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final LineService lineService;
    private final StationService stationService;

    @Transactional
    public LineResponse addSection(Long lineId, SectionRequest request) {
        var line = lineService.findLine(lineId);

        var upStation = stationService.findStation(request.getUpStationId());
        var downStation = stationService.findStation(request.getDownStationId());

        line.getSections().addSection(Section.builder()
            .upStation(upStation)
            .downStation(downStation)
            .distance(request.getDistance())
            .build());

        return LineResponse.from(line);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        var sections = lineService.findLine(lineId).getSections();

        sections.deleteSection(stationId);
    }
}
