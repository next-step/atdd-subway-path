package atdd.line.service;

import atdd.edge.domain.Edge;
import atdd.edge.domain.EdgeRepository;
import atdd.line.domain.Line;
import atdd.line.domain.LineRepository;
import atdd.line.dto.CreateEdgesAndStationsRequest;
import atdd.line.dto.CreateLineAndStationsRequest;
import atdd.line.dto.CreateLineRequest;
import atdd.line.dto.FindLineResponse;
import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import atdd.station.dto.CreateStationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LineServiceImpl implements LineService {

	private LineRepository lineRepository;
	private StationRepository stationRepository;
	private EdgeRepository edgeRepository;

	public LineServiceImpl(final LineRepository lineRepository, final StationRepository stationRepository,
						   final EdgeRepository edgeRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
		this.edgeRepository = edgeRepository;
	}

	@Override
	@Transactional
	public Line save(CreateLineRequest request) {
		return lineRepository.save(request.toEntity());
	}

	@Override
	@Transactional
	public Line saveLineAndStations(final CreateLineAndStationsRequest request) {
		Line line = request.getLine();
		List<CreateEdgesAndStationsRequest> edgesAndStationsRequests = request.getEdgesAndStations();
		for (CreateEdgesAndStationsRequest edgesAndStations : edgesAndStationsRequests) {
			CreateStationRequest sourceStationRequest = edgesAndStations.getSourceStation();
			CreateStationRequest targetStationRequest = edgesAndStations.getTargetStation();

			Station sourceStation = stationRepository.save(sourceStationRequest.toEntity());
			Station targetStation = stationRepository.save(targetStationRequest.toEntity());
			Edge edge = edgesAndStations.getEdge().toEntity(sourceStation, targetStation);
			edge.applyStations(sourceStation, targetStation);
			edge.applyLine(line);
			edgeRepository.save(edge);
		}
		return lineRepository.save(line);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Line> findAll() {
		return lineRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public FindLineResponse findByName(String name) {
		Line line = lineRepository.findByName(name)
			.orElseThrow(() -> new EntityNotFoundException("해당 노선이 없습니다."));
		List<Edge> edges = edgeRepository.findAllByLine(line.getId());
		List<Station> stations = getStationsUsing(edges);
		return FindLineResponse.builder()
			.line(line)
			.stations(stations)
			.build();
	}

	private List<Station> getStationsUsing(final List<Edge> edges) {
		List<Station> stations = new ArrayList<>();
		for (Edge edge : edges) {
			Station sourceStation = edge.getSourceStation();
			Station targetStation = edge.getTargetStation();
			addToListOnlyIfNotExists(stations, sourceStation, targetStation);
		}
		return stations;
	}

	private void addToListOnlyIfNotExists(final List<Station> stations,
										  final Station sourceStation, final Station targetStation) {
		if (!stations.contains(sourceStation)) {
			stations.add(sourceStation);
		}
		if (!stations.contains(targetStation)) {
			stations.add(targetStation);
		}
	}
}
