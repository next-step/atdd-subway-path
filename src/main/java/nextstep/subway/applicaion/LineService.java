package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SubwayRuntimeException;
import nextstep.subway.exception.message.SubwayErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line.addSection(upStation, downStation, request.getDistance());
        }
        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository
                .findAll()
                .stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return createLineResponse(lineRepository.findById(id)
                .orElseThrow(() -> new SubwayRuntimeException(SubwayErrorCode.NOT_FOUND_STATION.getMessage())));
    }

    public Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new SubwayRuntimeException(SubwayErrorCode.NOT_FOUND_STATION.getMessage()));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new SubwayRuntimeException(SubwayErrorCode.NOT_FOUND_STATION.getMessage()));

        if (lineRequest.getName() != null) {
            line.setName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            line.setColor(lineRequest.getColor());
        }
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = findLine(lineId);

        line.addSection(upStation, downStation, sectionRequest.getDistance());
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), createStationResponses(line));
    }

    private List<StationResponse> createStationResponses(Line line) {
        return line.getStations()
                .stream()
                .map(stationService::createStationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        Line line = findLine(lineId);
        line.deleteSection(stationId);
    }
}
