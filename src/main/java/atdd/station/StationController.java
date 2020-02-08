package atdd.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {

	private StationRepository stationRepository;

	public StationController(final StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	@PostMapping("/stations")
	public ResponseEntity<CreateStationRequest> createStation(@RequestBody CreateStationRequest request) {
		Station savedStation = stationRepository.save(request.toEntity());
		Long id = savedStation.getId();

		return ResponseEntity.created(URI.create("/stations/" + id))
			.contentType(MediaType.APPLICATION_JSON)
			.body(request);
	}

	@GetMapping("/stations")
	public ResponseEntity<List<Station>> getAll() {
		return ResponseEntity.ok(stationRepository.findAll());
	}
}
