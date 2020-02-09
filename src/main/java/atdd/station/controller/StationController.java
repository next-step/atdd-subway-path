package atdd.station.controller;

import atdd.station.dto.StationRequestDto;
import atdd.station.dto.StationResponseDto;
import atdd.station.service.StationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(StationController.ROOT_URI)
public class StationController {

    public static final String ROOT_URI = "/stations";

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponseDto> create(@RequestBody @Valid StationRequestDto requestDto) {
        final StationResponseDto responseDto = stationService.create(requestDto.getName());

        return ResponseEntity.created(URI.create(ROOT_URI + "/" + responseDto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseDto);
    }

}
