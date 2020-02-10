package atdd.station.controller;

import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import atdd.station.dto.CreateStationRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {

	private StationRepository stationRepository;

	public StationController(final StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	@PostMapping("/stations")
	public ResponseEntity<Station> createStation(@RequestBody CreateStationRequest request) {
		Station savedStation = stationRepository.save(request.toEntity());
		Long id = savedStation.getId();

		return ResponseEntity.created(URI.create("/stations/" + id))
			.contentType(MediaType.APPLICATION_JSON)
			.body(savedStation);
	}

	@GetMapping("/stations")
	public ResponseEntity<List<Station>> getAll() {
		return ResponseEntity.ok(stationRepository.findAll());
	}

	@GetMapping("/station")
	public ResponseEntity<Station> findByName(@RequestParam("name") String name) {
		return ResponseEntity.ok(stationRepository.findByName(name)
			.orElseThrow(() -> new IllegalArgumentException("해당 지하철역이 없습니다."))
		);
	}

	@DeleteMapping("/station")
	public ResponseEntity<Station> deleteByName(@RequestParam("name") String name) {
		Station station = stationRepository.findByName(name)
			.orElseThrow(() -> new IllegalArgumentException("해당 지하철역이 없습니다."));
		stationRepository.delete(station);

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(station);
	}
}
