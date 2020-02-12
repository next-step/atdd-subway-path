package atdd.station.controller;

import atdd.station.service.StationService;
import atdd.station.domain.Station;
import atdd.station.dto.StationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/stations", produces = "application/json")
public class StationController
{
    private static final String BASE_STATION_URL = "/stations";

    @Resource(name = "stationService")
    private StationService stationService;

    @PostMapping("")
    public ResponseEntity<Station> createStations(@RequestBody StationDto stationDto)
    {
        Station createdStation = stationService.create(stationDto);
        return ResponseEntity.created(URI.create(BASE_STATION_URL + "/" + createdStation.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdStation);
    }

    @GetMapping("")
    public ResponseEntity findStations()
    {
        List<Station> stations = stationService.findStations();
        return new ResponseEntity(stations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity detailById(@PathVariable long id)
    {
        Optional<Station> detailStation = stationService.findById(id);
        return new ResponseEntity(detailStation, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteStation(@PathVariable long id)
    {
        stationService.deleteStationById(id);
    }
}
