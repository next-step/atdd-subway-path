package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.ShortestPathResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;
    private ShortestPathFindAlgorithm<Station, Line, Integer> shortestPathFindAlgorithm;

    public LineService(LineRepository lineRepository, StationService stationService, ShortestPathFindAlgorithm<Station, Line, Integer> shortestPathFindAlgorithm) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.shortestPathFindAlgorithm = shortestPathFindAlgorithm;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        PairedStations pairedStations = new PairedStations(upStation, downStation);

        Line line = new Line(request.getName(),
                request.getColor(),
                pairedStations,
                request.getDistance());

        lineRepository.save(line);

        return LineResponse.fromEntity(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.fromEntity(lineRepository.findById(id).orElseThrow(IllegalArgumentException::new));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        line.update(lineRequest.toEntity());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        line.addSection(new PairedStations(upStation, downStation), sectionRequest.getDistance());
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        Station station = stationService.findById(stationId);
        line.deleteSection(station);
    }

    public ShortestPathResponse findShortestPath(Long sourceStationId, Long targetStationId) {
        List<Line> lines = lineRepository.findAll();
        Station sourceStation = stationService.findById(sourceStationId);
        Station targetStation = stationService.findById(targetStationId);

        if (sourceStation.equals(targetStation)) {
            throw new IllegalArgumentException("출발역과 도착역이 동일합니다");
        }

        ShortestPath<Station, Integer> shortestPath = shortestPathFindAlgorithm.findShortestPath(sourceStation, targetStation, lines);

        if (shortestPath.isNotExistPath()) {
            throw new IllegalArgumentException("출발역에서 도착역까지 갈 수 있는 경로가 존재하지 않습니다");
        }

        return ShortestPathResponse.from(shortestPath);
    }
}
