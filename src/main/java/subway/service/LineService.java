package subway.service;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import subway.dto.response.StationResponse;
import subway.entity.Line;
import subway.entity.Section;
import subway.entity.Station;
import subway.repository.LineRepository;
import subway.repository.StationRepository;
import subway.dto.request.LineModifyRequest;
import subway.dto.request.LineRequest;
import subway.dto.response.LineResponse;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId()).orElse(null);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElse(null);

        var section = Section.builder()
            .upStation(upStation)
            .downStation(downStation)
            .distance(request.getDistance())
            .build();

        Line line = lineRepository.save(Line.builder()
                .name(request.getName())
                .color(request.getColor())
                .section(section)
                .build());

        return LineResponse.from(line);
    }

    public LineResponse findLineResponse(Long id) {
        return LineResponse.from(findLine(id));
    }


    public StationResponse toStationResponse(Station station) {
        return StationResponse.builder()
            .id(station.getId())
            .name(station.getName())
            .build();
    }
    public Line findLine(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
    }

    public List<LineResponse> findLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
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
