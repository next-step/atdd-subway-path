package atdd.station.controller;

import atdd.line.domain.Edge;
import atdd.line.service.LineService;
import atdd.station.api.request.CreateStationRequestView;
import atdd.station.api.response.StationLineResponse;
import atdd.station.api.response.StationsResponseView;
import atdd.station.api.response.StationResponseView;
import atdd.station.domain.Station;
import atdd.station.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@RequestMapping("/stations")
@RestController
public class StationController {

    public static final String STATION_URL = "/stations";

    private final StationService stationService;
    private final LineService lineService;

    @PostMapping
    public ResponseEntity<StationResponseView> createStation(@RequestBody CreateStationRequestView view) {
        final Station station = view.toStation();
        final Station persistStation = stationService.save(station);

        return ResponseEntity
                .created(URI.create(STATION_URL + "/" + persistStation.getId()))
                .body(new StationResponseView(persistStation));
    }

    @GetMapping
    public ResponseEntity<StationsResponseView> getStations() {
        final List<Station> stations = stationService.findAll();
        final List<StationResponseView> views = stations.stream().map(this::stationToView).collect(toList());

        return ResponseEntity.ok(new StationsResponseView(views));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponseView> getStation(@PathVariable("id") Long id) {
        final Station findStation = stationService.findStationById(id).orElseGet(Station::emptyStation);
        final StationResponseView view = stationToView(findStation);

        return ResponseEntity.ok(view);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteStation(@PathVariable("id") Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }

    private StationResponseView stationToView(Station station) {
        final List<Edge> edges = lineService.findEdgesByStationId(station.getId());
        final List<StationLineResponse> lines = edges.stream()
                .map(Edge::getLine)
                .distinct()
                .map(StationLineResponse::new)
                .collect(toList());

        return new StationResponseView(station, lines);
    }

}
