package nextstep.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.station.StationResponse;
import nextstep.subway.station.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line entity = request.toEntity();
        Line line = lineRepository.save(entity);
        return createLineResponse(line);
    }


    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(this::createLineResponse)
            .collect(Collectors.toList());
    }

    public LineResponse findLineResponseById(Long id) {

        return createLineResponse(findLineById(id));

    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                                  .orElseThrow(() -> new IllegalArgumentException("line을 찾을 수 없습니다."));

    }

    @Transactional
    public void updateLine(LineRequest request, Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(() -> new IllegalArgumentException("line을 찾을 수 없습니다."));

        line.updateLine(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private LineResponse createLineResponse(Line line) {
        List<StationResponse> stationResponses = stationService.findAllStationById(
            line.getAllStationId());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

}
