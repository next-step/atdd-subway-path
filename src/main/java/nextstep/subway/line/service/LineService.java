package nextstep.subway.line.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.line.dto.*;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.LineRepository;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.entity.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line savedLine = lineRepository.save(convertToLine(request));

        return LineResponse.from(savedLine);
    }

    public List<LineResponse> getLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("노선이 존재하지 않습니다. id:%s", id)));

        return LineResponse.from(line);
    }

    @Transactional
    public void updateLine(Long id, ModifyLineRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("노선이 존재하지 않습니다. id:%s", id)));

        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private Station getStation(Long lineDto) {
        Station upStation = stationRepository.findById(lineDto)
                .orElseThrow(() -> new IllegalArgumentException(String.format("역이 존재하지 않습니다. id:%s", lineDto)));
        return upStation;
    }

    private Line convertToLine(LineRequest request) {
        Station upStation = getStation(request.getUpStationId());
        Station downStation = getStation(request.getDownStationId());
        return new Line(request.getName(), request.getColor(), request.getDistance(), upStation, downStation);
    }
}
