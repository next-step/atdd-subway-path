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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LineService {
	private final LineRepository lineRepository;
	private final StationService stationService;

	@Transactional
	public LineResponse saveLine(LineRequest request) {
		Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
		addSection(line, request.getUpStationId(), request.getDownStationId(), request.getDistance());
		return LineResponse.from(line);
	}

	public List<LineResponse> findAllLineResponse() {
		return lineRepository.findAll().stream()
			.map(LineResponse::from)
			.collect(Collectors.toList());
	}

	public LineResponse findLineResponse(Long id) {
		return LineResponse.from(findLine(id));
	}

	@Transactional
	public void updateLine(Long id, LineRequest lineRequest) {
		Line line = findLine(id);
		line.update(lineRequest.getName(), lineRequest.getColor());
	}

	@Transactional
	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = findLine(lineId);
		addSection(line, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
			sectionRequest.getDistance());
	}

	private void addSection(Line line, Long upStationId, Long downStationId, int distance) {
		if (upStationId != null && downStationId != null && distance != 0) {
			Station upStation = stationService.findStation(upStationId);
			Station downStation = stationService.findStation(downStationId);
			line.addSection(new Section(line, upStation, downStation, distance));
		}
	}

	@Transactional
	public void deleteSection(Long lineId, Long stationId) {
		Line line = findLine(lineId);
		Station station = stationService.findStation(stationId);

		line.removeSection(station);
	}

	private Line findLine(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(IllegalArgumentException::new);
	}
}
