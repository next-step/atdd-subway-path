package atdd.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class LineController {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody Line line) {
        Line savedLine = lineRepository.save(line);
        String resultUri = String.format("/lines/%d", savedLine.getId());

        return ResponseEntity.created(URI.create(resultUri))
                             .body(savedLine);
    }

    @GetMapping("/lines")
    public ResponseEntity readLines() {
        List<Line> lines = new ArrayList<Line>();
        lineRepository.findAll()
                      .forEach(lines::add);

        return new ResponseEntity(lines, HttpStatus.OK);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity readLine(@PathVariable Long id) {
        Optional<Line> optionalLine = lineRepository.findById(id);

        return getResponseEntityForNullableObject(optionalLine);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineRepository.deleteById(id);

        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/lines/{lineId}/edge")
    public ResponseEntity createEdge(@PathVariable Long lineId, @RequestBody Edge edge) {

        String uri = String.format("/lines/%d/edge/%d", lineId, edge.getId());

        Line targetLine = lineRepository.findById(lineId)
                                        .orElse(null);

        Long sourceStationId = edge.getSourceStationId();
        Long targetStationId = edge.getTargetStationId();

        Station sourceStation = stationRepository.findById(sourceStationId)
                                                 .orElse(null);
        Station targetStation = stationRepository.findById(targetStationId)
                                                 .orElse(null);

        List<Station> stations = targetLine.getStations();

        addStation(stations, sourceStation, targetStation);

        lineRepository.save(targetLine);

        Set<Line> sourceStationLines = sourceStation.getLines();
        sourceStationLines.add(targetLine);
        Set<Line> targetStationLines = targetStation.getLines();
        targetStationLines.add(targetLine);

        stationRepository.save(sourceStation);
        stationRepository.save(targetStation);

        return ResponseEntity.created(URI.create(uri))
                             .body(edge);
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public ResponseEntity deleteStationFromLine(@PathVariable Long lineId, @PathVariable Long stationId) {
        Line targetLine = lineRepository.findById(lineId)
                                        .orElse(null);

        List<Station> stations = targetLine.getStations();
        stations.removeIf(station -> station.getId()
                                            .equals(stationId));

        lineRepository.save(targetLine);

        return ResponseEntity.ok()
                             .build();
    }


    private List<Station> addStation(List<Station> stations, Station sourceStation, Station targetStation) {
        Long sourceStationId = sourceStation.getId();
        Long targetStationId = targetStation.getId();

        boolean isUnsupportedRequest = sourceStationId.equals(targetStationId);
        if (isUnsupportedRequest) {
            return stations;
        }

        int stationSize = stations.size();
        boolean isEmpty = stationSize == 0;
        if (isEmpty) {
            stations.add(sourceStation);
            stations.add(targetStation);
            return stations;
        }

        List<Long> filteredStationIdList = stations.stream()
                                                   .map(Station::getId)
                                                   .filter(stationId -> {
                                                       return stationId.equals(sourceStation.getId()) || stationId.equals(targetStation.getId());
                                                   })
                                                   .collect(Collectors.toList());

        boolean isAlreadyRegistered = filteredStationIdList.size() == 2;

        if (isAlreadyRegistered) {
            return stations;
        }

        Station firstStation = stations.get(0);
        Station lastStation = stations.get(stationSize - 1);

        Long firstStationId = firstStation.getId();
        Long lastStationId = lastStation.getId();

        if (firstStationId.equals(sourceStationId)) {
            stations.add(0, targetStation);
            return stations;
        }

        if (firstStationId.equals(targetStationId)) {
            stations.add(0, sourceStation);
        }

        if (lastStationId.equals(sourceStationId)) {
            stations.add(targetStation);
            return stations;
        }

        if (lastStationId.equals(targetStationId)) {
            stations.add(sourceStation);
        }

        return stations;
    }


    private ResponseEntity getResponseEntityForNullableObject(Optional<?> optionalObject) {
        return optionalObject.map(object -> new ResponseEntity(object, HttpStatus.OK))
                             .orElseGet(() -> ResponseEntity.noContent()
                                                            .build());
    }

}
