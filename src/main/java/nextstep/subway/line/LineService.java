package nextstep.subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.station.StationRepository;

import java.util.List;

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
        Line line = this.lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        return LineResponse.of(line);
    }

    public List<Line> findAllLines() {
        return this.lineRepository.findAll();
    }

    public Line findLineById(Long id) {
        return getLine(id);
    }

    @Transactional
    public void deleteLineById(Long id) {
        this.lineRepository.deleteById(id);
    }


    @Transactional
    public void updateLineById(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    private Line getLine(Long id) {
        return this.lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 노선이 존재하지 않습니다."));
    }


}
