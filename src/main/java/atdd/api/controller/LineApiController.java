package atdd.api.controller;

import atdd.domain.stations.Line;
import atdd.serivce.stations.LineService;
import atdd.serivce.stations.StationsService;
import atdd.web.dto.line.LineCreateRequestDto;
import atdd.web.dto.line.LineListResponseDto;
import atdd.web.dto.line.LineResponseDto;
import atdd.web.dto.station.StationsListResponseDto;
import atdd.web.dto.station.StationsResponseDto;
import atdd.web.dto.station.StationsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/line/")
public class LineApiController {
    private final LineService lineService;

    @PostMapping
    public ResponseEntity<Line> create(@RequestBody LineCreateRequestDto requestDto) {
        Line line = lineService.create(requestDto);
        return ResponseEntity
                .created(URI.create("/line/" + line.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(line);
    }

    @DeleteMapping("{id}")
    public HttpStatus delete(@PathVariable Long id) {
        lineService.delete(id);
        return HttpStatus.NO_CONTENT;
    }

    @GetMapping
    public LineListResponseDto readList() {
        List<Line> list = lineService.readList();
        return new LineListResponseDto().toDtoEntity(list);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponseDto> read(@PathVariable Long id) {
        LineResponseDto dto = lineService.read(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


}
