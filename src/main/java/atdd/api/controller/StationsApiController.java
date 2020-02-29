package atdd.api.controller;

import atdd.serivce.stations.StationsService;
import atdd.web.dto.station.StationsListResponseDto;
import atdd.web.dto.station.StationsResponseDto;
import atdd.web.dto.station.StationsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/stations/")
public class StationsApiController {
    private final StationsService stationsService;

    @PostMapping
    public ResponseEntity<StationsResponseDto> create(@RequestBody StationsSaveRequestDto requestDto){
        StationsResponseDto dto=stationsService.create(requestDto);
        return ResponseEntity
                .created(URI.create("/stations/" + dto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    @DeleteMapping("{id}")
    public HttpStatus delete(@PathVariable Long id) {
        stationsService.delete(id);
        return HttpStatus.NO_CONTENT;
    }

    @GetMapping
    public StationsListResponseDto readList(){
        return stationsService.readList();
    }

    @GetMapping("{id}")
    public ResponseEntity<StationsResponseDto> read(@PathVariable Long id){
        StationsResponseDto dto=stationsService.read(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
