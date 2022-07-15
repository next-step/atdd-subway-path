package nextstep.subway.applicaion;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

@Service
@Transactional(readOnly = true)
public class LineService {
	private LineRepository lineRepository;
	private StationService stationService;
	private StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationService stationService,
		StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
		this.stationRepository = stationRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest request) {
		Line line = lineRepository.save(Line.of(request.getName(), request.getColor()));

		if (!request.isRegistrableOfSection()) {
			return createLineResponse(line);
		}

		Station upStation = findStationById(request.getUpStationId());
		Station downStation = findStationById(request.getDownStationId());
		line.addSection(upStation, downStation, request.getDistance());

		return createLineResponse(line);
	}

	private Station findStationById(long stationId) {
		return stationRepository.findById(stationId)
			.orElseThrow(IllegalArgumentException::new);
	}

	public List<LineResponse> showLines() {
		return lineRepository.findAll().stream()
			.map(this::createLineResponse)
			.collect(Collectors.toList());
	}

	public LineResponse findById(Long id) {
		return createLineResponse(lineRepository.findById(id).orElseThrow(IllegalArgumentException::new));
	}

	@Transactional
	public void updateLine(Long id, LineRequest lineRequest) {
		Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
		line.changeName(lineRequest.getName());
		line.changeColor(lineRequest.getColor());
	}

	@Transactional
	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void addSection(Long lineId, SectionRequest sectionRequest) {
		Station upStation = stationService.findById(sectionRequest.getUpStationId());
		Station downStation = stationService.findById(sectionRequest.getDownStationId());
		Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);

		line.getSections().add(new Section(line, upStation, downStation, sectionRequest.getDistance()));
	}

	private LineResponse createLineResponse(Line line) {
		return new LineResponse(
			line.getId(),
			line.getName(),
			line.getColor(),
			createStationResponses(line)
		);
	}

	private List<StationResponse> createStationResponses(Line line) {
		if (line.getSections().isEmpty()) {
			return Collections.emptyList();
		}

		List<Station> stations = line.getSections().stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		stations.add(0, line.getSections().get(0).getUpStation());

		return stations.stream()
			.map(it -> stationService.createStationResponse(it))
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteSection(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
		Station station = stationService.findById(stationId);

		if (!line.getSections().get(line.getSections().size() - 1).getDownStation().equals(station)) {
			throw new IllegalArgumentException();
		}

		line.getSections().remove(line.getSections().size() - 1);
	}
}
