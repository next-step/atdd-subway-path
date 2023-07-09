package nextstep.subway.applicaion.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.line.request.SectionRequest;
import nextstep.subway.applicaion.line.response.LineResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.StationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public LineResponse appendSection(final Long lineId, final SectionRequest request) {
        final var line = lineRepository.getById(lineId);
        line.appendSection(convertToSection(line, request));

        return LineResponse.toResponse(line);
    }

    private Section convertToSection(final Line line, final SectionRequest request) {
        final var upStation = stationRepository.getById(request.getUpStationId());
        final var downStation = stationRepository.getById(request.getDownStationId());

        return new Section(line, upStation, downStation, request.getDistance());
    }

    @Transactional
    public void removeSection(final Long lineId, final Long stationId) {
        final var line = lineRepository.getById(lineId);
        final var station = stationRepository.getById(stationId);
        line.removeSection(station);
    }
}
