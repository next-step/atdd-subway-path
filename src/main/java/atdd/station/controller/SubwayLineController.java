package atdd.station.controller;

import atdd.station.domain.SubwayLine;
import atdd.station.dto.subwayLine.SubwayLineCreateRequestDto;
import atdd.station.dto.subwayLine.SubwayLineDetailResponseDto;
import atdd.station.dto.subwayLine.SubwayLineListResponseDto;
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
    public ResponseEntity<SubwayLine> create(@RequestBody SubwayLineCreateRequestDto subwayLine) {
        SubwayLine createdSubwayLine = subwayLineService.create(subwayLine);

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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        return ResponseEntity.ok().build();
    }
}
