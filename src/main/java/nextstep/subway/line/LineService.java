package nextstep.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationNotFoundException;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(StationNotFoundException::new);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation,
                downStation, lineRequest.getDistance());
        return LineResponse.from(lineRepository.save(line));
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse searchById(Long id) {
        return LineResponse.from(
                lineRepository.findById(id)
                        .orElseThrow(LineNotFoundException::new));
    }

    @Transactional
    public void update(Long id, UpdateLineRequest updateLineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
        line.update(updateLineRequest.getName(), updateLineRequest.getColor());
    }

    @Transactional
    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(StationNotFoundException::new);
        line.addSection(upStation, downStation, sectionRequest.getDistance());
        return LineResponse.from(line);
    }

    @Transactional
    public void deleteSection(Long id, Long stationId) {
        Station downStreamTerminusStation = stationRepository.findById(stationId)
                .orElseThrow(StationNotFoundException::new);
        Line line = lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
        line.deleteSection(downStreamTerminusStation);
    }
}
