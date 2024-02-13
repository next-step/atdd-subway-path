package nextstep.subway.line;

import nextstep.subway.station.Station;
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
        Station startStation = this.stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException("상행역이 존재하지 않습니다."));
        Station endStation = this.stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("하행역이 존재하지 않습니다."));
        Line line = this.lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor(), startStation, endStation));
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
