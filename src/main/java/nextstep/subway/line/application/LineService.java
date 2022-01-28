package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.common.domain.model.exception.EntityNotFoundException;
import nextstep.subway.common.domain.model.exception.FieldDuplicateException;
import nextstep.subway.line.domain.dto.LineRequest;
import nextstep.subway.line.domain.dto.LineResponse;
import nextstep.subway.line.domain.dto.SectionRequest;
import nextstep.subway.line.domain.model.Line;
import nextstep.subway.line.domain.repository.LineRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.model.Station;

@RequiredArgsConstructor
@Service
@Transactional
public class LineService {
    private static final String ENTITY_NAME_FOR_EXCEPTION = "지하철 노선";

    private final LineRepository lineRepository;
    private final StationService stationService;

    public Line findByIdOrThrow(Long id) {
        return lineRepository.findById(id)
                             .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME_FOR_EXCEPTION));
    }

    public Line findByIdWithStationsOrThrow(Long id) {
        return lineRepository.findByIdWithStations(id)
                             .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME_FOR_EXCEPTION));
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = findByIdWithStationsOrThrow(id);

        return LineResponse.withStationsFrom(line);
    }

    public LineResponse saveLine(LineRequest request) {
        if (lineRepository.existsByName(request.getName())) {
            throw new FieldDuplicateException("노선 이름");
        }
        Line line = lineRepository.save(
            new Line(request.getName(), request.getColor())
        );
        if (request.hasSection()) {
            Station upStation = stationService.findByIdOrThrow(request.getUpStationId());
            Station downStation = stationService.findByIdOrThrow(request.getDownStationId());
            line.addSection(upStation, downStation, request.getDistance());
        }
        return LineResponse.withStationsFrom(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll()
                             .stream()
                             .map(LineResponse::notWithStationsFrom)
                             .collect(Collectors.toList());
    }


    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findByIdOrThrow(id);

        line.edit(lineRequest.getName(), lineRequest.getColor());
    }


    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest request) {
        Station upStation = stationService.findByIdOrThrow(request.getUpStationId());
        Station downStation = stationService.findByIdOrThrow(request.getDownStationId());
        Line line = findByIdWithStationsOrThrow(lineId);

        line.addSection(upStation, downStation, request.getDistance());
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = findByIdOrThrow(lineId);

        line.deleteSection(stationId);
    }
}
