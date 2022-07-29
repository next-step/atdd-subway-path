package nextstep.subway.applicaion;

import static nextstep.subway.common.exception.errorcode.BusinessErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

@Service
public class PathService {

	private StationRepository stationRepository;
	private LineRepository lineRepository;

	public PathService(StationRepository stationRepository, LineRepository lineRepository) {
		this.stationRepository = stationRepository;
		this.lineRepository = lineRepository;
	}

	public PathResponse getPath(PathRequest pathRequest) {
		pathRequest.validationOfStation();
		Station sourceStation = getStation(pathRequest.getSource());
		Station targetStation = getStation(pathRequest.getTarget());

		PathFinder pathFinder = new PathFinder(getAllSections());
		List<Station> stationList = pathFinder.getShortestPath(sourceStation, targetStation);
		long distance = pathFinder.getSumOfDistance(sourceStation, targetStation);

		return new PathResponse(distance, createStationResponse(stationList));
	}

	private List<Section> getAllSections() {
		return lineRepository.findAll()
			.stream()
			.flatMap(line -> line.getSections().stream())
			.collect(Collectors.toList());
	}

	private Station getStation(long stationId) {
		return stationRepository.findById(stationId)
			.orElseThrow(() -> new BusinessException(ENTITY_NOT_FOUND));
	}

	private List<StationResponse> createStationResponse(List<Station> stationList) {
		return stationList.stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
	}
}
