package subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.*;
import subway.exception.EntityNotFoundException;
import subway.service.dto.StationLineCreateRequest;
import subway.service.dto.StationLineResponse;
import subway.service.dto.StationLineUpdateRequest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationLineService {
	private final StationRepository stationRepository;
	private final StationLineRepository stationLineRepository;

	@Transactional
	public StationLineResponse createStationLine(StationLineCreateRequest request) {
		final Station upStation = stationRepository.findById(request.getUpStationId())
			.orElseThrow(() -> new EntityNotFoundException("upStation not found"));

		final Station downStation = stationRepository.findById(request.getDownStationId())
			.orElseThrow(() -> new EntityNotFoundException("downStation not found"));

		final StationLine stationLine = StationLine.builder()
			.name(request.getName())
			.color(request.getColor())
			.upStation(upStation)
			.downStation(downStation)
			.distance(request.getDistance())
			.build();

		return StationLineResponse.fromEntity(stationLineRepository.save(stationLine));
	}

	@Transactional(readOnly = true)
	public List<StationLineResponse> getStationLines() {
		return stationLineRepository.findAll()
			.stream()
			.filter(Objects::nonNull)
			.map(StationLineResponse::fromEntity)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public StationLineResponse getStationLine(Long lineId) {
		return stationLineRepository.findById(lineId)
			.map(StationLineResponse::fromEntity)
			.orElseThrow(() -> new EntityNotFoundException("station line not found"));
	}

	@Transactional
	public void updateStationLine(Long lineId, StationLineUpdateRequest request) {
		final StationLine stationLine = stationLineRepository.findById(lineId)
			.orElseThrow(() -> new EntityNotFoundException("station line not found"));

		stationLine.update(request.getName(), request.getColor());
	}

	@Transactional
	public void deleteStationLine(Long lineId) {
		final StationLine stationLine = stationLineRepository.findById(lineId)
			.orElseThrow(() -> new EntityNotFoundException("station line not found"));

		stationLineRepository.delete(stationLine);
	}

	@Transactional
	public void createStationLineSection(Long lineId, StationLineSectionCreateRequest request) {
		final Station upStation = stationRepository.findById(request.getUpStationId())
			.orElseThrow(() -> new EntityNotFoundException("upStation not found"));

		final Station downStation = stationRepository.findById(request.getDownStationId())
			.orElseThrow(() -> new EntityNotFoundException("downStation not found"));

		final StationLine stationLine = stationLineRepository.findById(lineId)
			.orElseThrow(() -> new EntityNotFoundException("station line not found"));

		stationLine.createSection(upStation, downStation, request.getDistance());
	}

	@Transactional
	public void deleteStationLineSection(Long lineId, Long stationId) {
		final Station targetStation = stationRepository.findById(stationId)
			.orElseThrow(() -> new EntityNotFoundException("upStation not found"));

		final StationLine stationLine = stationLineRepository.findById(lineId)
			.orElseThrow(() -> new EntityNotFoundException("station line not found"));

		stationLine.deleteSection(targetStation);
	}
}
