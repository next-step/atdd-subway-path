package nextstep.subway.line;

import nextstep.subway.station.Station;
import nextstep.subway.station.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LineService {
    private final StationService stationService;
    private final LineRepository lineRepository;
    private final LineConverter lineConverter;

    public LineService(StationService stationService, LineRepository lineRepository, LineConverter lineConverter) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
        this.lineConverter = lineConverter;
    }


    @Transactional
    public LineResponse create(LineRequest request) {
        Station upStation = stationService.getStation(request.getUpStationId());
        Station downStation = stationService.getStation(request.getDownStationId());
        Line newLine = Line.of(request.getName(), request.getColor(), upStation, downStation, request.getDistance());
        lineRepository.save(newLine);
        return lineConverter.convert(newLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getList() {
        List<Line> lines = lineRepository.findAll();
        return lineConverter.convertToList(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse getById(Long id) {
        return lineConverter.convert(getLine(id));
    }


    @Transactional
    public void update(Long id, LineRequest request) {
        Line line = getLine(id);
        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void delete(Long id) {
        lineRepository.delete(getLine(id));
    }

    public Line getLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("line is not existed by id > " + id));
    }

    public List<Line> getAllLines() {
        return lineRepository.findAll();
    }
}
