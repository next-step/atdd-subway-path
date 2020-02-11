package atdd.station.web;

import atdd.station.domain.station.Station;
import atdd.station.domain.station.StationRepository;
import atdd.station.web.dto.StationListResponseDto;
import atdd.station.web.dto.StationRequestDto;
import atdd.station.web.dto.StationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class StationController {
    @Autowired
    StationRepository stationRepository;

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

        stationRepository.save(Station.builder().name(stationRequestDto.getName()).build());

        Station entity = stationRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("해당역이존재하지않습니다."));
        StationResponseDto stationResponseDto = new StationResponseDto(entity);

        return ResponseEntity.created(location)
                .header(String.valueOf(responseHeaders))
                .body(stationResponseDto);
    }

    @GetMapping("selectStationList")
    public ResponseEntity selectStation(){
        Logger logger = Logger.getLogger("selectStation");
        stationRepository.save(Station.builder().name("강남역").build());
        stationRepository.save(Station.builder().name("수서역").build());

        List<StationResponseDto> stationList = stationRepository
                .findAll()
                .stream()
                .map(StationResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationList);
    }

}
