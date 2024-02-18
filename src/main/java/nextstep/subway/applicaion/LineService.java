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

        this.addSection(line.getId(), upStation.getId(), downStation.getId(), request.getDistance());

        return LineResponse.from(
            line,
            line.getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList())
        );
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
            .map(line -> LineResponse.from(
                line,
                line.getStations().stream()
                    .map(StationResponse::from)
                    .collect(Collectors.toList()))
            )
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new BusinessException("노선 정보를 찾을 수 없습니다."));

        return LineResponse.from(
            line,
            line.getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList())
        );
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

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(() -> new BusinessException("노선 정보를 찾을 수 없습니다."));

        line.removeSection(stationId);
    }
}
