package nextstep.subway.section.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import subway.line.domain.Line;
import subway.line.repository.LineRepository;
import subway.line.service.LineService;
import subway.section.domain.Section;
import subway.section.model.SectionCreateRequest;
import subway.station.service.StationService;

@Service
@Slf4j
@RequiredArgsConstructor
public class SectionCreateService {
    private final StationService stationService;
    private final LineService lineService;
    private final LineRepository lineRepository;

    @Transactional
    public Section create(Long lineId, SectionCreateRequest request) {
        Line line = lineService.getLine(lineId);

        Section section = Section.builder()
                                 .downStation(stationService.get(request.getDownStationId()))
                                 .upStation(stationService.get(request.getUpStationId()))
                                 .distance(request.getDistance())
                                 .build();

        line.addSection(section);

        Line createdLine = lineRepository.save(line);

        return createdLine.getSection(request.getDownStationId(), request.getUpStationId());
    }
}
