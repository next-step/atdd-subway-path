package atdd.station.controller;

import atdd.station.domain.Line;
import atdd.station.dto.LineDto;
import atdd.station.service.LineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/lines", produces = "application/json")
public class LineController
{
    private static final String BASE_LINE_URL = "/lines";

    @Resource(name = "lineService")
    private LineService lineService;

    @PostMapping("")
    public ResponseEntity<Line> createLines(@RequestBody LineDto lineDto)
    {
        Line createdLine = lineService.create(lineDto);
        return ResponseEntity.created(URI.create(BASE_LINE_URL + "/" + createdLine.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdLine);
    }

    @GetMapping("")
    public ResponseEntity<Line> findLines()
    {
        List<Line> lines = lineService.findLines();
        return new ResponseEntity(lines, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity detailById(@PathVariable long id)
    {
        Optional<Line> detailLine = lineService.findById(id);
        return new ResponseEntity(detailLine, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteLine(@PathVariable long id)
    {
        lineService.deleteLineById(id);
    }
}
