package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (request.isSaveLineRequestValid()) {
            Section section = sectionService.createSection(line, request);
            line.addSection(section);
        }
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.of(findLineById(id));
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);

        line.update(lineRequest.toLine());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        line.addSection(sectionRequest.toSection());
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = stationService.findById(stationId);

        // TODO 지우기
        if (!line.getSections().get(line.getSections().size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        line.deleteSection(stationId);
    }
}
