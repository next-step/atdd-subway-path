package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.applicaion.dto.CreateLineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.UpdateLineRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.error.ErrorCode;
import nextstep.subway.error.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LineService {

    private final StationService stationService;
    private final LineRepository lineRepository;

    @Transactional
    public LineResponse saveLine(final CreateLineRequest request) {
        final Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            final Station upStation = stationService.findById(request.getUpStationId());
            final Station downStation = stationService.findById(request.getDownStationId());
            line.addSection(upStation, downStation, request.getDistance());
        }
        return new LineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findById(final Long id) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.LINE_NOT_FOUND));
        return new LineResponse(line);
    }

    @Transactional
    public void updateLine(final Long id, final UpdateLineRequest updateLineRequest) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.LINE_NOT_FOUND));
        line.update(updateLineRequest.getName(), updateLineRequest.getColor());
    }

    @Transactional
    public void deleteLine(final Long id) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.LINE_NOT_FOUND));
        lineRepository.delete(line);
    }

    @Transactional
    public void addSection(final Long lineId, final AddSectionRequest addSectionRequest) {
        final Station upStation = stationService.findById(addSectionRequest.getUpStationId());
        final Station downStation = stationService.findById(addSectionRequest.getDownStationId());
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LINE_NOT_FOUND));

        line.addSection(upStation, downStation, addSectionRequest.getDistance());
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LINE_NOT_FOUND));
        final Station station = stationService.findById(stationId);
        line.removeSection(station);
    }
}
