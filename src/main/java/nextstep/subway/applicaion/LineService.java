package nextstep.subway.applicaion;

import lombok.val;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.DuplicatedException;
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
        checkDuplicatedName(request);

        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        Line line = lineRepository.save(
                Line.builder()
                        .name(request.getName())
                        .color(request.getColor())
                        .upStation(upStation)
                        .downStation(downStation)
                        .distance(request.getDistance())
                        .build()
        );
        return createLineResponse(line);
    }

    private void checkDuplicatedName(LineRequest request) {
        if (lineRepository.existsByName(request.getName())) {
            throw new DuplicatedException();
        }
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = lineRepository.getById(id);

        return createLineResponse(line);
    }

    private LineResponse createLineResponse(Line line) {
        List<StationResponse> stationResponses = stationService.createStationResponses(line.getAllStations());

        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .createdDate(line.getCreatedDate())
                .modifiedDate(line.getModifiedDate())
                .stations(stationResponses)
                .build();
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.getById(id);

        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        Line line = lineRepository.getById(id);

        lineRepository.deleteById(line.getId());
    }

    public void saveSection(Long id, SectionRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        Line line = lineRepository.getById(id);

        line.addSection(upStation, downStation, request.getDistance());
    }

    public void deleteSection(Long id, Long stationId) {
        Line line = lineRepository.getById(id);

        line.removeSection(stationId);
    }

    public Lines findAll() {
        val lines = lineRepository.findAll();
        return new Lines(lines);
    }
}
