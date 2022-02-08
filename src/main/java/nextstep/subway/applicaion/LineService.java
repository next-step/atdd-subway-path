package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.*;
import nextstep.subway.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (!request.isSectionNull()) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line.addSections(upStation, downStation, request.getDistance());
        }

        return createLineResponse(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return createLineResponse(getLine(id));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.updateLine(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = getLine(lineId);

        line.addSections(upStation, downStation, sectionRequest.getDistance());
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = getLine(lineId);
        Station station = stationService.findById(stationId);
        line.removeSection(station);
    }

    public List<Line> getLines() {
        return lineRepository.findAll();
    }

    private Line getLine(Long id) {
        return lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
    }

    public PathResponse getPath(Long source, Long target) {
        List<Line> lines = lineRepository.findAll();
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);

        PathFinder pathFinder = new PathFinder(lines);
        int shortestDistance = pathFinder.getShortestDistance(sourceStation, targetStation);
        List<Station> shortestPath = pathFinder.getShortestPath(sourceStation, targetStation);

        PathResponse pathResponse = createPathResponse(shortestPath, shortestDistance);
        return createPathResponse(shortestPath, shortestDistance);
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                createStationResponses(line),
                line.getCreatedDate(),
                line.getModifiedDate()
        );
    }

    private List<StationResponse> createStationResponses(Line line) {
        return line.getStations()
                .stream()
                .map(it -> stationService.createStationResponse(it))
                .collect(Collectors.toList());
    }

    private PathResponse createPathResponse(List<Station> stations, int distance) {
        return new PathResponse(
                createStationResponses(stations),
                distance);
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        return stations.stream()
                .map(it -> stationService.createStationResponse(it))
                .collect(Collectors.toList());
    }
}
