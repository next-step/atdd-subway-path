package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import nextstep.subway.line.application.dto.LineRequest;
import nextstep.subway.line.application.dto.LineResponse;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSection;
import nextstep.subway.line.domain.entity.LineSections;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {

        Line line = lineRepository.save(
            new Line(lineRequest.getName(), lineRequest.getColor(), new LineSections()));
        LineSection lineSection = new LineSection(
            line,
            stationRepository.findByIdOrThrow(lineRequest.getUpStationId()),
            stationRepository.findByIdOrThrow(lineRequest.getDownStationId()),
            lineRequest.getDistance());

        line.addSection(lineSection);

        return getLineResponseByLine(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(this::getLineResponseByLine)
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long lineId) {
        Line line = lineRepository.findByIdOrThrow(lineId);

        return getLineResponseByLine(line);
    }

    @Transactional
    public void updateLine(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findByIdOrThrow(lineId);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long lineId) {
        Line line = lineRepository.findByIdOrThrow(lineId);
        lineRepository.delete(line);
    }

    private LineResponse getLineResponseByLine(Line line) {
        return createLineResponse(line, getStationResponsesByLine(line));
    }

    private List<StationResponse> getStationResponsesByLine(Line line) {
        List<Station> stations = line.getLineSections().getStations();

        return stations.stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    private LineResponse createLineResponse(Line line, List<StationResponse> stations) {
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            stations
        );
    }

}
