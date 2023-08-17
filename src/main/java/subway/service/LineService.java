package subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.exception.impl.LineNotFoundException;
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
    public Line saveLine(LineRequest request) {
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());

        return lineRepository.save(request.toLine(upStation, downStation));
    }

    public List<Line> findAllLines() {
        return lineRepository.findAll();
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
    }

    @Transactional
    public Line updateLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        return line.update(request);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public Line addSection(Long id, SectionRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());

        line.addSection(request.toSection(line, upStation, downStation));
        return line;
    }

    @Transactional
    public void removeSection(Long id, Long stationId) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        Station station = stationService.findStationById(stationId);

        line.removeSection(station);
    }
}
