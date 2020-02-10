package atdd.station.web;

import atdd.station.web.dto.StationRequestDto;
import atdd.station.web.dto.StationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.logging.Logger;

@RequiredArgsConstructor
@RestController
public class StationController {

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
        Logger logger = Logger.getLogger("stationRequestDto");
        logger.info(stationRequestDto.getName());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        URI location = URI.create("/createStation");

        StationResponseDto stationResponseDto = StationResponseDto.builder().name(stationRequestDto.getName()).build();

        logger.info("stationRequestDto.getName() : "+ stationRequestDto.getName());
        logger.info("name :: " + stationResponseDto.getName());
        return ResponseEntity.created(location)
                .header(String.valueOf(responseHeaders))
                .body(stationResponseDto);
    }

}
