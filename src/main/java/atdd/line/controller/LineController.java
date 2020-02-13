package atdd.line.controller;

import atdd.line.api.request.CreateEdgeRequestView;
import atdd.line.api.request.CreateLineRequestView;
import atdd.line.api.response.LineListResponseView;
import atdd.line.api.response.LineResponseView;
import atdd.line.api.response.LineStationResponse;
import atdd.line.domain.Edge;
import atdd.line.domain.Line;
import atdd.line.service.LineService;
import atdd.station.domain.Station;
import atdd.station.service.StationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;
    private final StationService stationService;

    @PostMapping
    public ResponseEntity<LineResponseView> createLine(@RequestBody CreateLineRequestView view, HttpServletRequest request) {
        final Line line = view.toLine();
        final Line persistLine = lineService.saveLine(line);

        return ResponseEntity
                .created(URI.create(request.getServletPath() + "/" + persistLine.getId()))
                .body(new LineResponseView(persistLine, emptyList()));
    }

    @PostMapping("/{id}/edges")
    public ResponseEntity<LineResponseView> createEdge(@PathVariable("id") Long lineId,
                                                       @RequestBody CreateEdgeRequestView view,
                                                       HttpServletRequest request) {

        final Edge edge = viewToEdge(lineId, view);
        final Line persistLine = lineService.saveEdge(edge);

        final LineResponseView lineResponseView = lineToView(persistLine);

        return ResponseEntity
                .created(URI.create(request.getServletPath() + "/" + persistLine.getId()))
                .body(lineResponseView);
    }

    @GetMapping
    public ResponseEntity<LineListResponseView> getLines() {
        final List<Line> lines = lineService.findAll();
        final List<LineResponseView> views = lines.stream().map(this::lineToView).collect(toList());

        return ResponseEntity.ok(new LineListResponseView(lines.size(), views));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponseView> getLine(@PathVariable("id") Long id) {
        final Line findLine = lineService.findLineById(id);
        final LineResponseView lineResponseView = lineToView(findLine);

        return ResponseEntity.ok(lineResponseView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteLine(@PathVariable("id") Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity<Object> deleteLineStation(@PathVariable("lineId") Long lineId,
                                                    @PathVariable("stationId") Long stationId) {

        lineService.deleteEdge(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    private Edge viewToEdge(Long lineId, CreateEdgeRequestView view) {
        final Line findLine = lineService.findLineById(lineId);
        final Station sourceStation = stationService.findStationById(view.getSourceStationId());
        final Station targetStation = stationService.findStationById(view.getTargetStationId());

        return Edge.builder()
                .line(findLine)
                .elapsedTime(view.getElapsedTime())
                .distance(view.getDistance())
                .sourceStation(sourceStation)
                .targetStation(targetStation)
                .build();
    }

    private LineResponseView lineToView(Line line) {
        final List<Edge> findEdges = lineService.findEdgesByLineId(line.getId());
        final List<LineStationResponse> stations = CollectionUtils.isEmpty(findEdges)
                                                    ? emptyList() : edgesToStations(findEdges);

        return new LineResponseView(line, stations);
    }

    private List<LineStationResponse> edgesToStations(List<Edge> edges) {
        // 종점 목록만 가져온다.
        final List<LineStationResponse> stations = edges.stream()
                .map(Edge::getTargetStation)
                .map(LineStationResponse::new)
                .collect(toList());

        // 마지막 출발점역을 가져온다.
        getLastSourceStation(edges)
                .ifPresent(stations::add);

        return stations;
    }

    private Optional<LineStationResponse> getLastSourceStation(List<Edge> edges) {
        final int lastIndex = edges.size() - 1;
        return edges.stream()
                .skip(lastIndex)
                .map(Edge::getSourceStation)
                .map(LineStationResponse::new)
                .findFirst();
    }

}
