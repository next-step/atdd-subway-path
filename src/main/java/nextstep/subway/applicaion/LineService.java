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
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

@Service
@Transactional(readOnly = true)
public class LineService {
	private LineRepository lineRepository;
	private StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
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
		return createLineResponse(lineRepository.findById(id)
			.orElseThrow(IllegalArgumentException::new));
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

		Line line = lineRepository.findById(lineId)
			.orElseThrow(IllegalArgumentException::new);

		Station upStation = findStationById(sectionRequest.getUpStationId());
		Station downStation = findStationById(sectionRequest.getDownStationId());

		line.addSection(upStation, downStation, sectionRequest.getDistance());

	}

	private LineResponse createLineResponse(Line line) {
		return new LineResponse(
			line.getId(),
			line.getName(),
			line.getColor(),
			createStationResponses(line)
		);
	}

	private StationResponse createStationResponse(Station station) {
		return new StationResponse(
			station.getId(),
			station.getName()
		);
	}

	private List<StationResponse> createStationResponses(Line line) {
		if (line.getSections().isEmpty()) {
			return Collections.emptyList();
		}

		return line.getStations()
			.stream()
			.map(this::createStationResponse)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteSection(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId)
			.orElseThrow(IllegalArgumentException::new);
		Station station = findStationById(stationId);
		line.removeSection(station);
	}
}
