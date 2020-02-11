package atdd.station;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = StationUri.ROOT)
@RestController
public class StationController {

    private final StationService stationService;

    @PostMapping
    public ResponseEntity create(@RequestBody StationDto.Request request) {

        Station station = stationService.create(request.toEntity());

        return ResponseEntity.created(StationUri.getCreatedUrl(station.getId())).contentType(MediaType.APPLICATION_JSON).build();

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity get(@PathVariable("id") Station station) {

        return ResponseEntity.ok().body(station);
    }
}
