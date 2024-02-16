package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Line line = lineRepository.save(request.to());

        this.addSection(line.getId(), upStation.getId(), downStation.getId(), line.getDistance());

        return LineResponse.from(line, getStations(line));
    }

    public List<LineResponse> showLines() {
        // TODO fetch join
        return lineRepository.findAll().stream()
            .map(line -> LineResponse.from(line, getStations(line)))
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        // TODO fetch join
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new BusinessException("노선 정보를 찾을 수 없습니다."));

        return LineResponse.from(line, getStations(line));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new BusinessException("노선 정보를 찾을 수 없습니다."));

        line.updateLine(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);

        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new BusinessException("노선 정보를 찾을 수 없습니다."));

        line.addSection(new Section(line, upStation, downStation, distance));
    }

    private List<StationResponse> getStations(Line line) {
        if (line.getSections().isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getSections().stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        stations.add(0, line.getSections().get(0).getUpStation());

        return stations.stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new BusinessException("노선 정보를 찾을 수 없습니다."));

        final var section = line.getSections().stream()
            .filter(it -> it.getDownStation().getId().equals(stationId))
            .findAny()
            .orElseThrow(() -> new BusinessException("구간 정보를 찾을 수 없습니다."));

        line.removeSection(section);
    }
}
