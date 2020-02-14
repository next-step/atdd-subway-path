package atdd.station.controller;

import atdd.station.dto.station.StationCreateRequestDto;
import atdd.station.dto.station.StationCreateResponseDto;
import atdd.station.dto.station.StationDetailResponseDto;
import atdd.station.dto.station.StationListResponseDto;
import atdd.station.service.StationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping(value = "/stations",
        produces = "application/json")
public class StationController {

    @Resource(name = "stationService")
    private StationService stationService;

    @PostMapping("/")
    public ResponseEntity<StationCreateResponseDto> create(@RequestBody StationCreateRequestDto station) {
        StationCreateResponseDto createdStation = stationService.create(station);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/stations/" + createdStation.getId()));
        return new ResponseEntity<>(createdStation, headers, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<StationListResponseDto> list() {
        StationListResponseDto stations = stationService.list();
        return new ResponseEntity<>(stations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationDetailResponseDto> detail(@PathVariable long id) {
        StationDetailResponseDto station = stationService.detail(id);
        return new ResponseEntity<>(station, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        stationService.delete(id);
    }
}
