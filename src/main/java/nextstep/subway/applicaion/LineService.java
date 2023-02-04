package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final LineSectionService lineSectionService;

    public LineService(LineRepository lineRepository, LineSectionService lineSectionService) {
        this.lineRepository = lineRepository;
        this.lineSectionService = lineSectionService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(request.toEntity());
        lineSectionService.addSection(line, new SectionRequest(request));
        return LineResponse.toResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::toResponse)
                .collect(Collectors.toList());
    }

    public LineResponse getById(Long id) {
        return LineResponse.toResponse(findById(id));
    }

    public Line findById(Long id) {
        return lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.updateLine(lineRequest.toEntity());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findById(lineId);
        lineSectionService.addSection(line, sectionRequest);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findById(lineId);
        lineSectionService.deleteSection(line, stationId);
    }
}
