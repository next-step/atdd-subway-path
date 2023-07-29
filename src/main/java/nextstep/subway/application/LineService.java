package nextstep.subway.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionAddRequest;
import nextstep.subway.exception.LineDuplicationNameException;
import nextstep.subway.exception.LineNotFoundException;

@Service
@Transactional(readOnly = true)
public class LineService {

	private final LineRepository lineRepository;
	private final StationService stationService;

	public LineService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	@Transactional
	public LineResponse save(LineCreateRequest lineCreateRequest) {
		validateDuplicationLineName(lineCreateRequest.getName());
		Line line = lineRepository.save(lineCreateRequest.toLine());
		addSection(line, lineCreateRequest.getUpStationId(), lineCreateRequest.getDownStationId(),
			lineCreateRequest.getDistance());
		return LineResponse.from(line);
	}

	private void validateDuplicationLineName(String name) {
		if (lineRepository.existsByName(name)) {
			throw new LineDuplicationNameException();
		}
	}

	private void addSection(Line line, Long upStationId, Long DownStationId, Integer distance) {
		Station upStation = stationService.findById(upStationId);
		Station downStation = stationService.findById(DownStationId);
		line.addSection(upStation, downStation, distance);
	}

	public List<Line> findAll() {
		return lineRepository.findAll();
	}

	public LineResponse findById(Long id) {
		return LineResponse.from(lineRepository.findById(id)
			.orElseThrow(LineNotFoundException::new));
	}

	@Transactional
	public void update(Long id, LineUpdateRequest lineUpdateRequest) {
		Line line = lineRepository.findById(id)
			.orElseThrow(LineNotFoundException::new);
		line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
	}

	@Transactional
	public void delete(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public LineResponse addSection(Long id, SectionAddRequest sectionAddRequest) {
		Line line = lineRepository.findById(id)
			.orElseThrow(LineNotFoundException::new);
		addSection(line, sectionAddRequest.getUpStationId(), sectionAddRequest.getDownStationId(),
			sectionAddRequest.getDistance());
		return LineResponse.from(line);
	}

	@Transactional
	public void deleteSection(Long id, Long stationId) {
		Line line = lineRepository.findById(id)
			.orElseThrow(LineNotFoundException::new);
		line.removeSection(stationId);
	}
}
