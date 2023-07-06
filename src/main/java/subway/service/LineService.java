package subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import subway.entity.Line;
import subway.entity.Station;
import subway.repository.LineRepository;
import subway.repository.StationRepository;
import subway.request.LineModifyRequest;
import subway.request.LineRequest;
import subway.response.LineResponse;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId()).orElse(null);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElse(null);
        Line line = lineRepository.save(Line.builder()
                .name(request.getName())
                .color(request.getColor())
                .upStation(upStation)
                .downStation(downStation)
                .distance(request.getDistance())
                .build());

        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .stations(line.getStations())
                .build();
    }

    public LineResponse findLine(Long id) {
        return lineRepository.findById(id).map(o -> LineResponse.builder()
                .id(o.getId())
                .name(o.getName())
                .color(o.getColor())
                .stations(o.getStations())
                .build())
                .orElse(null);
    }

    public List<LineResponse> findLines() {
        return lineRepository.findAll().stream().map(o -> LineResponse.builder()
                        .id(o.getId())
                        .name(o.getName())
                        .color(o.getColor())
                        .stations(o.getStations())
                        .build()).collect(Collectors.toList());
    }

    @Transactional
    public void modifyLine(Long id, LineModifyRequest request) {
        lineRepository.findById(id).ifPresent(o -> o.modify(request));
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.findById(id).ifPresent(o -> lineRepository.delete(o));
    }
}
