package atdd.station.web;

import atdd.station.domain.station.Station;
import atdd.station.domain.station.StationRepository;
import atdd.station.service.StationService;
import atdd.station.web.dto.StationRequestDto;
import atdd.station.web.dto.StationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class StationController {
    @Autowired
    StationRepository stationRepository;
    @Autowired
    StationService stationService;
    @PostMapping("/stations")
    public @ResponseBody ResponseEntity stations(@RequestBody String inputJson) {
        Logger logger = Logger.getLogger("station");
        logger.info(inputJson);

        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.set("Content-Type", "application/json");

        URI location = URI.create("/stations");
        return ResponseEntity.created(location)
                .headers(responseHeaders)
                .body(inputJson);
    }

    @PostMapping("createStation")
    public ResponseEntity createStation(@RequestBody StationRequestDto stationRequestDto) {
        Logger logger = Logger.getLogger("createStation");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        URI location = URI.create("/createStation");

         Long id =stationService.create(stationRequestDto);

        return ResponseEntity.created(location)
                .header(String.valueOf(responseHeaders))
                .body(id);
    }

    @GetMapping("selectStationList")
    public ResponseEntity selectStationList(){

        stationService.create(StationRequestDto.builder().name("강남역").build());
        stationService.create(StationRequestDto.builder().name("수서역").build());

        List<Station> stationList = stationService.select();

        return ResponseEntity.ok().body(stationList);
    }

    @GetMapping("selectStation/{id}")
    public ResponseEntity selectStation(@PathVariable Long id) {

        stationService.create(StationRequestDto.builder().name("강남역").build());

        StationResponseDto stationResponseDto = stationService.findById(id);

        return  ResponseEntity.ok().body(stationResponseDto);
    }
}
