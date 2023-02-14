package nextstep.subway.line;

import lombok.RequiredArgsConstructor;
import nextstep.subway.section.SectionService;
import nextstep.subway.section.SectionCreateRequest;
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
		sectionService.addSection(saveLine.getId(), new SectionCreateRequest(lineRequest));
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
		return createLineResponse(findLineEntityById(id));
    }

    @Transactional
	public void updateLineById(Long id, LineUpdateRequest lineUpdateRequest) {
		Line line = findLineEntityById(id);
		line.updateLine(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
	}

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private LineResponse createLineResponse(Line line) {
		return new LineResponse(line);
    }

	@Transactional(readOnly = true)
	public Line findLineEntityById(Long id) {
		return lineRepository.findById(id).orElseThrow(() -> new NullPointerException("Line doesn't exist"));
	}
}
