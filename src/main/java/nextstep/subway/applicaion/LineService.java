package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.dto.PathResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationService stationService, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = new Line(request.getName(), request.getColor());
        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line = new Line(request.getName(), request.getColor());
            Section section = new Section(line, downStation, upStation, request.getDistance());
            line.initSection(section);
        }
        lineRepository.save(line);
        return new LineResponse(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return new LineResponse(lineRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (lineRequest.getName() != null) {
            line.setName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            line.setColor(lineRequest.getColor());
        }
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        Section section = new Section(line, downStation, upStation, sectionRequest.getDistance());
        line.addSection(section);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        line.deleteSection(stationId);
    }

    public PathResponse getShortestPath(Long source, Long target) {
        List<Line> allLines = lineRepository.findAll();
        Lines lines = new Lines(allLines);

        List<StationResponse> allStations = stationService.findAllStations();
        List<Long> shortestPath = lines.calculateShortestPath(source, target, allStations);

        List<StationResponse> stationResponses = shortestPath.stream()
                .map(stationId -> new StationResponse(stationService.findById(stationId)))
                .collect(Collectors.toList());

        int shortestDistance = lines.getShortestDistance(stationResponses);

        return new PathResponse(stationResponses, shortestDistance);
    }

    //TODO filter가 안되는 이유 찾기.
    private boolean isMatchSection(StationResponse stationA, StationResponse stationB, List<Section> allSections) {
        return allSections.stream()
                .filter(section ->
                        (section.getDownStation().getId() == stationA.getId() &&
                                section.getUpStation().getId() == stationB.getId()) ||
                                (section.getUpStation().getId() == stationA.getId() &&
                                        section.getDownStation().getId() == stationB.getId()))
                .findFirst().isPresent();
    }
};