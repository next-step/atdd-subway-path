package atdd.api.controller;

import atdd.domain.stations.Stations;
import atdd.serivce.stations.StationsService;
import atdd.web.dto.StationsListResponseDto;
import atdd.web.dto.StationsResponseDto;
import atdd.web.dto.StationsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpResponseException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/stations/")
public class StationsApiController {

    private final StationsService stationsService;

    @PostMapping("")
    public ResponseEntity create(@RequestBody StationsSaveRequestDto requestDto){

        Stations stations=stationsService.create(requestDto);
        return ResponseEntity
                .created(URI.create("/stations/" + stations.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(stations);
    }

    @DeleteMapping("{id}")
    public HttpStatus delete(@PathVariable Long id) {
        stationsService.delete(id);
        return HttpStatus.NO_CONTENT;
    }

    @GetMapping("")
    public List<StationsListResponseDto> list(){
        return stationsService.getList();
    }

    @GetMapping("{id}")
    public StationsResponseDto detail(@PathVariable Long id){
        return stationsService.findById(id);
    }

}
