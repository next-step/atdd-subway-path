package atdd.station.controller;

import atdd.station.dto.PathResponseDto;
import atdd.station.dto.StationCreateRequestDto;
import atdd.station.dto.StationResponseDto;
import atdd.station.service.StationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(StationController.ROOT_URI)
public class StationController {

    public static final String ROOT_URI = "/stations";

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponseDto> create(@RequestBody @Valid StationCreateRequestDto requestDto) {
        final StationResponseDto responseDto = stationService.create(requestDto.getName());

        return ResponseEntity.created(URI.create(ROOT_URI + "/" + responseDto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseDto);
    }

    @GetMapping
    public List<StationResponseDto> findAll() {
        return stationService.findAll();
    }

    @GetMapping("/{id}")
    public StationResponseDto getStation(@PathVariable Long id) {
        return stationService.getStation(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        stationService.delete(id);
    }

    @GetMapping("/shortest-path")
    public PathResponseDto getShortestPath(@RequestParam Long startStationId, @RequestParam Long endStationId) {
        return stationService.getShortestPath(startStationId, endStationId);
    }

}
