package atdd.station.controller;

import atdd.station.domain.SubwayLine;
import atdd.station.dto.subwayLine.*;
import atdd.station.service.SubwayLineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/subway-lines",
        produces = "application/json")
public class SubwayLineController {
    private SubwayLineService subwayLineService;

    public SubwayLineController(SubwayLineService subwayLineService) {
        this.subwayLineService = subwayLineService;
    }


    @PostMapping("/")
    public ResponseEntity<SubwayLineCreateResponseDto> create(@RequestBody SubwayLineCreateRequestDto subwayLine) {
        SubwayLineCreateResponseDto createdSubwayLine = subwayLineService.create(subwayLine);
        return ResponseEntity.created(URI.create("/subway-lines/" + createdSubwayLine.getId())).body(createdSubwayLine);
    }

    @GetMapping("/")
    public ResponseEntity<SubwayLineListResponseDto> list() {
        SubwayLineListResponseDto subwayLines = subwayLineService.list();
        return new ResponseEntity<>(subwayLines, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubwayLineDetailResponseDto> detail(@PathVariable int id) {
        SubwayLineDetailResponseDto subwayLine = subwayLineService.detail(id);
        return new ResponseEntity<>(subwayLine, HttpStatus.OK);
    }

    @PostMapping("/{id}/subways")
    public ResponseEntity<SubwayLine> update(@PathVariable int id, @RequestBody SubwayLineUpdateRequestDto stations) {
        SubwayLine updatedSubwayLine = subwayLineService.update(id, stations);
        return new ResponseEntity<>(updatedSubwayLine, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        subwayLineService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{subwayLineId}/stations/{stationName}")
    public ResponseEntity<Void> deleteStation(@PathVariable int subwayLineId, @PathVariable String stationName) {
        subwayLineService.deleteStation(subwayLineId, stationName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
