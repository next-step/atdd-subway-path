package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line.addSection(upStation, downStation, request.getDistance());
        }
        return LineResponse.from(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = findLine(id);
        return LineResponse.from(line);
    }

    private Line findLine(Long id) {
        return lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    public Lines getLines() {
        List<Line> lines = lineRepository.findAll();
        return new Lines(lines);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLine(id);

        line.changeName(lineRequest.getName());
        line.changeColor(lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = findLine(lineId);

        line.addSection(upStation, downStation, sectionRequest.getDistance());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLine(lineId);
        Station station = stationService.findById(stationId);
        line.deleteSection(station);
    }
}
