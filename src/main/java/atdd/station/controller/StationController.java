package atdd.station.controller;

import atdd.station.model.CreateStationRequestView;
import atdd.station.model.dto.StationDto;
import atdd.station.model.dto.StationLineDto;
import atdd.station.model.entity.Station;
import atdd.station.repository.StationRepository;
import atdd.station.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stations")
public class StationController {
    private static final Logger logger = LoggerFactory.getLogger(StationController.class);

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationService stationService;

    @PostMapping
    public ResponseEntity<StationDto> createStation(@RequestBody CreateStationRequestView view) {
        final Station station = stationRepository.save(view.toStation());

        StationDto stationDto = StationDto.builder()
                .id(station.getId())
                .name(station.getName()).build();

        return ResponseEntity.created(URI.create("/stations/" + station.getId()))
                .body(stationDto);
    }

    @GetMapping
    public ResponseEntity<List<StationDto>> findAllStations() {
        final List<Station> stations = stationRepository.findAll();

        List<StationDto> stationDtos = new ArrayList<>();

        for (Station station : stations) {
            List<StationLineDto> lines = stationService.lineDtos(station.getLineIds());

            StationDto stationDto = StationDto.builder()
                    .id(station.getId())
                    .name(station.getName())
                    .lines(lines).build();

            stationDtos.add(stationDto);
        }

        return ResponseEntity.ok(stationDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationDto> findStation(@PathVariable long id) {
        final Optional<Station> optionalStation = stationRepository.findById(id);

        if (optionalStation.isPresent()) {
            Station station = optionalStation.get();

            StationDto stationDto = StationDto.builder()
                    .id(station.getId())
                    .name(station.getName())
                    .lines(stationService.lineDtos(station.getLineIds())).build();


            return ResponseEntity.ok(stationDto);
        }

        return ResponseEntity
                .noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable long id) {
        stationRepository.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
