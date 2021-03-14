package nextstep.subway.line.application;

import nextstep.subway.error.NameExistsException;
import nextstep.subway.error.NotFoundException;
import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
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
        if (checkExistsName(request.getName())) {
            throw new NameExistsException(request.getName());
        }
        return LineResponse.of(createLine(request));
    }

    private Line createLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        addSection(persistLine, request.getUpStationId(),
                request.getDownStationId(), request.getDistance());

        return lineRepository.save(persistLine);
    }

    public Line addSection(Line line, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findByStation(upStationId);
        Station downStation = stationService.findByStation(downStationId);

        line.addSection(upStation, downStation, distance);
        return lineRepository.save(line);
    }

    private boolean checkExistsName(String name) {
        return lineRepository.findByName(name).isPresent();
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long lineId) {
        return LineResponse.of(findLine(lineId));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public void updateLineById(LineRequest lineRequest, Long lineId) {
        Line persistLine = findLine(lineId);

        persistLine.update(lineRepository.save(lineRequest.toLine()));
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long lineId) {
        Line persistLine = findLine(lineId);
        lineRepository.delete(persistLine);
    }

    public LineResponse addSection(Long lineId, SectionRequest request) {
        Line line = findLine(lineId);
        addSection(line, request.getUpStationId(),
                request.getDownStationId(), request.getDistance());

        return LineResponse.of(lineRepository.save(line));
    }

    public LineResponse removeSection(Long lineId, Long stationId) {
        Line line = findLine(lineId);
        Station lastStation = stationService.findByStation(stationId);

        line.removeSection(lastStation);
        return LineResponse.of(lineRepository.save(line));
    }

    @Transactional(readOnly = true)
    public Line findLine(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundException(lineId));
    }
}
