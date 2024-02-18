package nextstep.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.application.dto.LineCreateRequest;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.application.dto.LineUpdateRequest;
import nextstep.subway.application.dto.SectionCreateRequest;
import nextstep.subway.domain.BusinessException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService { // usecase

    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse createLine(LineCreateRequest lineCreateRequest) {
        Line line = lineRepository.save(lineCreateRequest.to());
        if (lineCreateRequest.getUpStationId() != null && lineCreateRequest.getDownStationId() != null && lineCreateRequest.getDistance() != 0) {
            line.addSection(
                    stationService.retrieveById(lineCreateRequest.getUpStationId())
                            .orElseThrow(() -> new BusinessException("존재하지 않는 역입니다.")),
                    stationService.retrieveById(lineCreateRequest.getDownStationId())
                            .orElseThrow(() -> new BusinessException("존재하지 않는 역입니다.")),
                    lineCreateRequest.getDistance()
            );
        }

        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse fineLineResponseById(Long id) {
        return lineRepository.findById(id)
                .map(this::createLineResponse)
                .orElseThrow(() -> new BusinessException("존재하지 않는 노선입니다."));
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new BusinessException("존재하지 않는 노선입니다."));
        line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public Optional<Line> retrieveById(Long lineId) {
        return lineRepository.findById(lineId);
    }

    @Transactional
    public void addSection(Long lineId, SectionCreateRequest request) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 노선입니다."));

        line.addSection(stationService.retrieveById(request.getUpStationId())
                        .orElseThrow(() -> new BusinessException("존재하지 않는 역입니다.")),
                stationService.retrieveById(request.getDownStationId())
                        .orElseThrow(() -> new BusinessException("존재하지 않는 역입니다.")),
                request.getDistance());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 노선입니다."));

        line.deleteSection(stationId);
    }

    private LineResponse createLineResponse(Line line) {
        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .build();
    }
}
