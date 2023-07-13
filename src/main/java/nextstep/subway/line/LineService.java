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
        Section section = saveSectionOf(lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
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
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        Section section = saveSectionOf(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        line.addSection(section);
        return LineResponse.from(line);
    }

    private Section saveSectionOf(Long sectionRequest, Long sectionRequest1, int sectionRequest2) {
        Station upstreamStation = stationRepository.findById(sectionRequest)
                .orElseThrow(StationNotFoundException::new);
        Station downstreamStation = stationRepository.findById(sectionRequest1)
                .orElseThrow(StationNotFoundException::new);
        return sectionRepository.save(new Section(null, upstreamStation, downstreamStation, sectionRequest2));
    }

    @Transactional
    public void deleteSection(Long id, Station downStreamTerminusStation) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        line.deleteStation(downStreamTerminusStation);
    }
}
