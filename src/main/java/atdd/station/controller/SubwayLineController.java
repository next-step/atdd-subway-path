package atdd.station.controller;

import atdd.station.domain.SubwayLine;
import atdd.station.dto.subwayLine.*;
import atdd.station.service.SubwayLineService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping(value = "/subway-lines",
        produces = "application/json")
public class SubwayLineController {

    @Resource(name = "subwayLineService")
    private SubwayLineService subwayLineService;

    @PostMapping("/")
    public ResponseEntity<SubwayLineCreateResponseDto> create(@RequestBody SubwayLineCreateRequestDto subwayLine) {
        SubwayLineCreateResponseDto createdSubwayLine = subwayLineService.create(subwayLine);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/subway-lines/" + createdSubwayLine.getId()));
        return new ResponseEntity<>(createdSubwayLine, headers, HttpStatus.CREATED);
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

    @PutMapping("/{id}")
    public ResponseEntity<SubwayLine> update(@PathVariable int id, @RequestBody SubwayLineUpdateRequestDto stations) {
        SubwayLine updatedSubwayLine = subwayLineService.update(id, stations);
        return new ResponseEntity<>(updatedSubwayLine, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        subwayLineService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/{name}")
    public ResponseEntity<Void> deleteStation(@PathVariable int id, @PathVariable String name) {
        subwayLineService.deleteStation(id, name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
