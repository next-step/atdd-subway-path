package atdd.station.web;

import atdd.station.web.dto.StationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.logging.Logger;

@RequiredArgsConstructor
@RestController
public class StationController {

    @PostMapping("/stations")
    public ResponseEntity stations(@RequestBody String inputJson) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        URI location = URI.create("/stations");
        return ResponseEntity.created(location)
                .headers(responseHeaders)
                .body(inputJson);

    }

}
