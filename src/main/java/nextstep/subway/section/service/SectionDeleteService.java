package nextstep.subway.section.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import subway.line.domain.Line;
import subway.line.service.LineService;

@Service
@Slf4j
@RequiredArgsConstructor
public class SectionDeleteService {
    private final LineService lineService;

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Line line = lineService.getLine(lineId);
        line.deleteSection(stationId);
    }
}
