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
import subway.dto.SectionResponse;
import subway.exception.impl.AlreadyExistDownStation;
import subway.exception.impl.LineNotFoundException;
import subway.exception.impl.NoMatchStationException;
import subway.exception.impl.NonLastStationDeleteNotAllowedException;
import subway.exception.impl.SingleSectionDeleteNotAllowedException;
import subway.exception.impl.StationNotFoundException;
import subway.repository.LineRepository;
import subway.repository.SectionRepository;
import subway.repository.StationRepository;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository,
        StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(StationNotFoundException::new);

        Line line = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.from(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        return LineResponse.from(line);
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
    public LineResponse createSection(Long id, SectionRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        Sections sections = line.getSections();
        if (sections.noMatchDownStation(request.getUpStationId())) {
            throw new NoMatchStationException();
        }

        if (sections.isStationExist(request.getDownStationId())) {
            throw new AlreadyExistDownStation();
        }

        Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(StationNotFoundException::new);

        Section section = request.toSection(line, upStation, downStation);
        line.addSection(section);

        return LineResponse.from(line);
    }

    @Transactional
    public void deleteSection(Long id, Long stationId) {
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
