package atdd.api.controller;

import atdd.domain.stations.Stations;
import atdd.serivce.stations.StationsService;
import atdd.web.dto.StationsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/stations")
public class StationsApiController {

    private final StationsService stationsService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody StationsSaveRequestDto requestDto){

        Stations stations=stationsService.create(requestDto);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/stations/" + stations.getId()));

        return new ResponseEntity<>(stations, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id) {
        stationsService.delete(id);
        return id;
    }
}
