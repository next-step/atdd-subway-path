package atdd.line.controller;

import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import atdd.line.service.LineService;
import atdd.station.dto.SectionCreateRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(LineController.ROOT_URI)
public class LineController {

    public static final String ROOT_URI = "/lines";

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponseDto> create(@RequestBody @Valid LineCreateRequestDto requestDto) {

        LineResponseDto responseDto = lineService.create(requestDto);

        return ResponseEntity.created(URI.create(ROOT_URI + "/" + responseDto.getId())).body(responseDto);
    }

    @GetMapping("/{lineId}")
    public LineResponseDto getLine(@PathVariable Long lineId) {
        return lineService.getLine(lineId);
    }

    @GetMapping
    public List<LineResponseDto> findAll(@RequestParam(required = false) String name) {
        return lineService.findAll(name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{lineId}")
    public void delete(@PathVariable Long lineId){
        lineService.delete(lineId);
    }

    @PutMapping("/{lineId}/stations/{stationId}")
    public void addStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.addStation(lineId, stationId);
    }

    @PutMapping("/{lineId}/stations/{stationId}/sections")
    public void addSection(@PathVariable Long lineId, @PathVariable Long stationId, @Valid @RequestBody SectionCreateRequestDto requestDto) {
        lineService.addSection(lineId, stationId, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{lineId}/stations/{stationId}")
    public void deleteStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.deleteStation(lineId, stationId);
    }
}
