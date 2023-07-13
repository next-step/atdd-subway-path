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
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository,
            SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(StationNotFoundException::new);
        Section section = sectionRepository.save(new Section(null, upStation, downStation, lineRequest.getDistance()));
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(),
                List.of(section));
        return LineResponse.from(lineRepository.save(line));
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse searchById(Long id) {
        return LineResponse.from(
                lineRepository.findById(id).orElseThrow(LineNotFoundException::new));
    }

    @Transactional
    public void update(Long id, UpdateLineRequest updateLineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        line.update(updateLineRequest.getName(), updateLineRequest.getColor());
    }

    @Transactional
    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Station upstreamStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(StationNotFoundException::new);
        Station downstreamStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(StationNotFoundException::new);
        Section section = sectionRepository.save(new Section(null, upstreamStation, downstreamStation, sectionRequest.getDistance()));
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        line.addSection(section);
        return LineResponse.from(line);
    }

    @Transactional
    public void deleteSection(Long id, Station downStreamTerminusStation) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        line.deleteSection(downStreamTerminusStation);
    }
}
