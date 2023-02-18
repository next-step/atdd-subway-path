package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        createFirstSection(request, line);

        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(final Long lineId) {
        return createLineResponse(findLineById(lineId));
    }

    public Line findLineById(final Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public void updateLine(final Long lineId, final LineRequest lineRequest) {
        Line line = findLineById(lineId);

        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(final Long lineId) {
        lineRepository.deleteById(lineId);
    }


    private LineResponse createLineResponse(final Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                createStationResponses(line)
        );
    }

    private List<StationResponse> createStationResponses(final Line line) {
        if (line.isEmptySections()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getAllStations();

        return stations.stream()
                .map(stationService::createStationResponse)
                .collect(Collectors.toList());
    }

    private void createFirstSection(final LineRequest request, final Line line) {
        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line.addSection(new Section(line, upStation, downStation, request.getDistance()));
        }
    }
}
