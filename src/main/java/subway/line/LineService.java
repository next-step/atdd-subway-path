package subway.line;

import static java.util.stream.Collectors.*;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import subway.dto.line.LineResponse;
import subway.dto.line.LineUpdateRequest;
import subway.station.Station;

@Transactional(readOnly = true)
@Service
public class LineService {
	private final LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	private Line findById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);
	}

	public LineResponse findLineById(Long id) {
		Line line = findById(id);
		return LineResponse.of(line);
	}

	public List<LineResponse> lines() {
		List<Line> lines = lineRepository.findAll();
		return lines.stream()
			.map(LineResponse::of)
			.collect(toList());
	}

	@Transactional
	public LineResponse save(Line line, Station upStation, Station downStation, Integer distance) {
		Line savedLine = lineRepository.save(line);
		savedLine.addSection(new Section(savedLine, upStation, downStation, distance));

		return LineResponse.of(savedLine);
	}

	@Transactional
	public void update(Long id, LineUpdateRequest request) {
		Line line = findById(id);
		line.changeName(request.getName());
		line.changeColor(request.getColor());
	}

	@Transactional
	public void delete(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public LineResponse addSection(Long id, Station upStation, Station downStation, Integer distance) {
		Line line = findById(id);
		line.addSection(new Section(line, upStation, downStation, distance));
		return LineResponse.of(line);
	}

	@Transactional
	public void deleteSection(Long id, Station deleteTargetStation) {
		Line line = findById(id);
		line.removeFinalStation(deleteTargetStation);
	}
}
