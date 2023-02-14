package nextstep.subway.line;

import lombok.RequiredArgsConstructor;
import nextstep.subway.section.SectionService;
import nextstep.subway.section.SectionCreateRequest;
import nextstep.subway.section.Section;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LineService {

	private final SectionService sectionService;
	private final LineRepository lineRepository;

    @Transactional
    public LineResponse saveLine(LineCreateRequest lineRequest) {
		Line saveLine = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));

		Section newSection = sectionService.saveSection(new SectionCreateRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
		saveLine.addSection(newSection);
		newSection.updateLine(saveLine);
        return createLineResponse(saveLine);
    }

	@Transactional(readOnly = true)
    public List<LineResponse> showLines() {
		return lineRepository.findAll().stream()
			.map(this::createLineResponse)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
		return createLineResponse(lineRepository.findById(id).orElseThrow(() -> new NullPointerException("Line doesn't exist")));
    }

    @Transactional
	public void updateLineById(Long id, LineUpdateRequest lineUpdateRequest) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new NullPointerException("Line doesn't exist"));
		line.updateLine(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
	}

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private LineResponse createLineResponse(Line line) {
		return new LineResponse(line);
    }

	// Mock Test Method
	public Line findById(Long id) {
		return lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
	}
}
