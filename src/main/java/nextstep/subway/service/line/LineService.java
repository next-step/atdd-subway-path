package nextstep.subway.service.line;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.line.PathFinder;
import nextstep.subway.domain.line.ShortPath;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import nextstep.subway.service.line.request.LineCreateRequest;
import nextstep.subway.service.line.request.LineModifyRequest;
import nextstep.subway.service.line.request.SectionAddRequest;
import nextstep.subway.service.line.response.LineResponse;
import nextstep.subway.service.line.response.ShortPathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public LineService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest lineCreateRequest) {

        Line line = new Line(lineCreateRequest.getName(),
                lineCreateRequest.getColor(),
                stationRepository.findById(lineCreateRequest.getUpStationId()).orElseThrow(StationNotFoundException::new),
                stationRepository.findById(lineCreateRequest.getDownStationId()).orElseThrow(StationNotFoundException::new),
                lineCreateRequest.getDistance()
        );

        lineRepository.save(line);
        return LineResponse.of(line);
    }

    @Transactional
    public void modifyLine(Long id, LineModifyRequest lineModifyRequest) {
        lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new)
                .modify(lineModifyRequest.getName(), lineModifyRequest.getColor());
    }

    public List<LineResponse> findLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(LineNotFoundException::new);
    }

    public ShortPathResponse findShortPath(Long startStationId, Long endStationId) {
        Station startStation = stationRepository.findById(startStationId)
                .orElseThrow(StationNotFoundException::new);

        Station endStation = stationRepository.findById(endStationId)
                .orElseThrow(StationNotFoundException::new);

        PathFinder pathFinder = new PathFinder(lineRepository.findAll());
        ShortPath shortPath = pathFinder.findShortPath(startStation, endStation);
        return ShortPathResponse.of(shortPath);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionAddRequest sectionAddRequest) {
        Station upStation = stationRepository.findById(sectionAddRequest.getUpStationId())
                .orElseThrow(StationNotFoundException::new);

        Station downStation = stationRepository.findById(sectionAddRequest.getDownStationId())
                .orElseThrow(StationNotFoundException::new);

        lineRepository.findById(lineId)
                .orElseThrow(LineNotFoundException::new)
                .addSection(upStation, downStation, sectionAddRequest.getDistance());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(StationNotFoundException::new);

        lineRepository.findById(lineId)
                .orElseThrow(LineNotFoundException::new)
                .removeSection(station);
    }

}
