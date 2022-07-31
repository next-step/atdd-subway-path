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
	public LineResponse save(LineRequest request) {
		Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
		addSection(line,
			new SectionRequest(request.getUpStationId(), request.getDownStationId(), request.getDistance()));
		return LineResponse.from(line);
	}

	public List<LineResponse> findAllById() {
		return lineRepository.findAll().stream()
			.map(LineResponse::from)
			.collect(Collectors.toList());
	}

	public LineResponse findById(Long id) {
		return LineResponse.from(findLine(id));
	}

	@Transactional
	public void update(Long id, LineRequest lineRequest) {
		Line line = findLine(id);
		line.update(lineRequest.getName(), lineRequest.getColor());
	}

	@Transactional
	public void delete(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = findLine(lineId);
		addSection(line, sectionRequest);
	}

	private void addSection(Line line, SectionRequest sectionRequest) {
		if (!sectionAddable(sectionRequest)) {
			return;
		}
		Station upStation = stationService.findStation(sectionRequest.getUpStationId());
		Station downStation = stationService.findStation(sectionRequest.getDownStationId());
		line.addSection(new Section(line, upStation, downStation, sectionRequest.getDistance()));
	}

	private boolean sectionAddable(SectionRequest request) {
		return request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0;
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
