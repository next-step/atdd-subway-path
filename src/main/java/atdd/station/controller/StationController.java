package atdd.station.controller;

import atdd.station.domain.Station;
import atdd.station.dto.StationCreateRequestDto;
import atdd.station.dto.StationDetailResponseDto;
import atdd.station.dto.StationListResponseDto;
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

    @PostMapping("/create")
    public ResponseEntity<Station> create(@RequestBody StationCreateRequestDto station) {
        Station createdStation = stationService.create(station);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/stations/list/" + createdStation.getId()));
        return new ResponseEntity<>(createdStation, headers, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<StationListResponseDto> list() {
        StationListResponseDto stations = stationService.list();
        return new ResponseEntity<>(stations, HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<StationDetailResponseDto> detail(@PathVariable long id) {
        StationDetailResponseDto station = stationService.findById(id);
        return new ResponseEntity<>(station, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        stationService.delete(id);
    }
}
