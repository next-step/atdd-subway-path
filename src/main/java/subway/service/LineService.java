package subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;
import subway.exception.impl.AlreadyExistDownStation;
import subway.exception.impl.LineNotFoundException;
import subway.exception.impl.NoMatchStationException;
import subway.exception.impl.NonLastStationDeleteNotAllowedException;
import subway.exception.impl.SingleSectionDeleteNotAllowedException;
import subway.exception.impl.StationNotFoundException;
import subway.repository.LineRepository;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findStation(request.getUpStationId());
        Station downStation = stationService.findStation(request.getDownStationId());

        Line line = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.from(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        line.update(request);
        return LineResponse.from(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public Line addSection(Long id, SectionRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        Sections sections = line.getSections();
        if (sections.noMatchDownStation(request.getUpStationId())) {
            throw new NoMatchStationException();
        }

        if (sections.isStationExist(request.getDownStationId())) {
            throw new AlreadyExistDownStation();
        }

        Station upStation = stationService.findStation(request.getUpStationId());
        Station downStation = stationService.findStation(request.getDownStationId());

        Section section = request.toSection(line, upStation, downStation);
        line.addSection(section);

        return line;
    }

    @Transactional
    public void removeSection(Long id, Long stationId) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        Sections sections = line.getSections();

        if (sections.hasSingleSection()) {
            throw new SingleSectionDeleteNotAllowedException();
        }

        if (sections.isNotLastStation(stationId)) {
            throw new NonLastStationDeleteNotAllowedException();
        }

        line.removeSection();
    }
}
