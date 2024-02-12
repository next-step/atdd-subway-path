package nextstep.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getDistance());
        Section section = new Section(line, upStation, downStation, lineRequest.getDistance());

        line.addSection(section);
        Line result = lineRepository.save(line);
        return new LineResponse(result);
    }

    private Station findStationById(long id) {
        return stationRepository.findById(id).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다"));
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("아이디에 맞는 노선이 존재하지 않습니다"));
    }

    public List<LineResponse> findAll() {
        List<Line> stationLines = lineRepository.findAll();

        return stationLines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateLineById(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("아이디에 맞는 노선이 존재하지 않습니다"));

        line.updateLine(lineRequest);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void registerSection(Line line, Section section) {
        line.addSection(section);
    }

    public void deleteSection(Line line, Station station) {
        line.deleteSection(station);
    }

    public boolean isExistSection(Line line, Section section) {
        return line.isExistSection(section);
    }

    public boolean isConnectedSection(Line line, Section section) {
        return line.isConnectedSection(section);
    }
}
