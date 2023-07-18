package nextstep.subway.applicaion.line;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.line.request.LineCreateRequest;
import nextstep.subway.applicaion.line.request.LineUpdateRequest;
import nextstep.subway.applicaion.line.request.SectionRequest;
import nextstep.subway.applicaion.line.response.LineResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.support.SubwayShortestPath;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public LineResponse saveLine(final LineCreateRequest request) {
        final var line = lineRepository.save(convertToLine(request));
        return convertToLineResponse(line);
    }

    private Line convertToLine(final LineCreateRequest request) {
        final var upStation = stationRepository.getById(request.getUpStationId());
        final var downStation = stationRepository.getById(request.getDownStationId());

        return new Line(
                request.getName(),
                request.getColor(),
                upStation,
                downStation,
                request.getDistance()
        );
    }

    @Transactional
    public void updateLine(final Long id, final LineUpdateRequest request) {
        final var line = lineRepository.getById(id);
        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse appendSection(final Long lineId, final SectionRequest request) {
        final var line = lineRepository.getById(lineId);
        line.appendSection(convertToSection(request));

        return convertToLineResponse(line);
    }

    private Section convertToSection(final SectionRequest request) {
        final var upStation = stationRepository.getById(request.getUpStationId());
        final var downStation = stationRepository.getById(request.getDownStationId());

        return new Section(upStation, downStation, request.getDistance());
    }

    @Transactional
    public void removeSection(final Long lineId, final Long stationId) {
        final var line = lineRepository.getById(lineId);
        final var station = stationRepository.getById(stationId);
        line.removeSection(station);
    }

    public List<LineResponse> findAllLines() {
        final var lines = lineRepository.findAll();
        return lines.stream()
                .map(this::convertToLineResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse findLine(final Long id) {
        final var line = lineRepository.getById(id);
        return convertToLineResponse(line);
    }

    public LineResponse convertToLineResponse(final Line line) {
        return LineResponse.toResponse(line, orderedStationsOf(line));
    }

    private List<Station> orderedStationsOf(final Line line) {
        final var shortestPath = SubwayShortestPath.builder(line.getStations(), line.getSections())
                .source(line.getFirstStation())
                .target(line.getLastStation())
                .build();

        return shortestPath.getStation();
    }
}
