package nextstep.subway.applicaion;

import static nextstep.subway.common.exception.errorcode.BusinessErrorCode.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.SectionResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
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

		if (request.isAbleAddSection()) {
			line.addSection(
				findStationById(request.getUpStationId()),
				findStationById(request.getDownStationId()),
				request.getDistance());
		}

		return createLineResponse(line);
	}

	private Station findStationById(long stationId) {
		return stationRepository.findById(stationId)
			.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));
	}

	public List<LineResponse> showLines() {
		return lineRepository.findAll().stream()
			.map(this::createLineResponse)
			.collect(Collectors.toList());
	}

	public List<SectionResponse> showSections() {
		return findAllSections().stream()
			.map(SectionResponse::of)
			.collect(Collectors.toList());
	}

	private List<Section> findAllSections() {
		return lineRepository.findAll()
			.stream()
			.flatMap(line -> line.getSections().stream())
			.collect(Collectors.toList());
	}

	public LineResponse findById(Long id) {
		return createLineResponse(lineRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND)));
	}

	@Transactional
	public void updateLine(Long lineId, LineRequest lineRequest) {
		Line line = findByLineId(lineId);
		line.changeName(lineRequest.getName());
		line.changeColor(lineRequest.getColor());
	}

	@Transactional
	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void addSection(Long lineId, SectionRequest sectionRequest) {

		Line line = findByLineId(lineId);
		Station upStation = findStationById(sectionRequest.getUpStationId());
		Station downStation = findStationById(sectionRequest.getDownStationId());

		line.addSection(upStation, downStation, sectionRequest.getDistance());

	}

	@Transactional
	public void deleteSection(Long lineId, Long stationId) {
		Line line = findByLineId(lineId);
		Station station = findStationById(stationId);
		line.removeSection(station);
	}

	private Line findByLineId(long lineId) {
		return lineRepository.findById(lineId)
			.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));
	}

	private LineResponse createLineResponse(Line line) {
		return LineResponse.of(line, createStationResponses(line));
	}

	private List<StationResponse> createStationResponses(Line line) {
		if (line.getSections().isEmpty()) {
			return Collections.emptyList();
		}

		return line.getStations()
			.stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
	}

}
