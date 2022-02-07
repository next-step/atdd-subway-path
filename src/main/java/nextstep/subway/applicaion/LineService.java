package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationService stationService, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = new Line(request.getName(), request.getColor());
        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line = new Line(request.getName(), request.getColor());
            Section section = new Section(line, downStation, upStation, request.getDistance());
            line.initSection(section);
        }
        lineRepository.save(line);
        return new LineResponse(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return new LineResponse(lineRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (lineRequest.getName() != null) {
            line.setName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            line.setColor(lineRequest.getColor());
        }
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        Section section = new Section(line, downStation, upStation, sectionRequest.getDistance());
        line.addSection(section);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        line.deleteSection(stationId);
    }
}
