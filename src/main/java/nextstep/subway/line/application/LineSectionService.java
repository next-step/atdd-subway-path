package nextstep.subway.line.application;

import lombok.AllArgsConstructor;
import nextstep.subway.line.application.dto.SectionRequest;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSection;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class LineSectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public void saveSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findByIdOrThrow(lineId);

        Station upStation = stationRepository.findByIdOrThrow(sectionRequest.getUpStationId());
        Station downStation = stationRepository.findByIdOrThrow(sectionRequest.getDownStationId());

        line.addSection(
            new LineSection(line, upStation, downStation, sectionRequest.getDistance()));
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findByIdOrThrow(lineId);
        Station station = stationRepository.findByIdOrThrow(stationId);

        line.deleteSection(station);
    }

}
