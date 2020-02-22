package atdd.line;


import atdd.edge.EdgeService;
import atdd.station.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequiredArgsConstructor
@RequestMapping(value = LineLink.ROOT)
@RestController
public class LineController {

    private final LineService lineService;
    private final EdgeService edgeService;

    @PostMapping
    public ResponseEntity create(@RequestBody LineDto.Request request) {
        Line createLine = lineService.create(request.toEntity());
        return ResponseEntity.created(LineLink.getCreatedUrl(createLine.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(createLine);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Line line) {
        if (line == null)
            return ResponseEntity.notFound().build();
        lineService.delete(line);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity find(@PathVariable("id") Line line) {
        Set<Station> stations = edgeService.findStationsByLine(line);
        return ResponseEntity.ok().body(LineDto.Response.from(line, stations));
    }
}
