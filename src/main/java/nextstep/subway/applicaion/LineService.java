package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.error.exception.EntityDuplicateException;
import nextstep.subway.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        checkDuplicated(request.getName());

        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        addSection(line.getId(),new SectionRequest(
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()));

        return LineResponse.of(line);
    }

    private void checkDuplicated(String name) {
        lineRepository.findByName(name).ifPresent(line -> {
            throw new EntityDuplicateException(name);
        });
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.of(line, getStationResponses(line)))
                .collect(Collectors.toList());
    }

    private List<StationResponse> getStationResponses(Line line) {
        return line.getStations()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = findLine(id);
        return LineResponse.of(line, getStationResponses(line));
    }

    private Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(id);
                });
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLine(id);
        line.edit(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLine(lineId);
        Section section = createSection(line, sectionRequest);

        line.addSection(section);
    }

    private Section createSection(Line line, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());

        return new Section(line, upStation, downStation, sectionRequest.getDistance());
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLine(lineId);
        Station station = stationService.findById(stationId);

        line.removeSection(station);
    }
}
