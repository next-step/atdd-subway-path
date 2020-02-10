package atdd.station.controller;

import atdd.station.model.CreateStationRequestView;
import atdd.station.model.Station;
import atdd.station.repository.StationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
public class StationController {
    private static final Logger logger = LoggerFactory.getLogger(StationController.class);

    @Autowired
    private StationRepository stationRepository;


    // TODO 지하철역 조회
    // TODO 지하철역 삭제
    @PostMapping(value = "/stations")
    @ResponseBody
    public ResponseEntity<Station> createStation(@RequestBody CreateStationRequestView view) {
        Station station = stationRepository.save(view.toStation());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.created(URI.create("/stations/" + station.getId()))
                .headers(httpHeaders)
                .body(station);
    }

    @GetMapping(value = "/stations")
    @ResponseBody
    public ResponseEntity<List<Station>> findAllStations() {
        List stations = stationRepository.findAll();

        return ResponseEntity.ok(stations);
    }
}
