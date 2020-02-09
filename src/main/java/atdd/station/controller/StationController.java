package atdd.station.controller;

import atdd.station.dto.StationRequestDto;
import atdd.station.dto.StationResponseDto;
import atdd.station.service.StationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

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

    @GetMapping
    public List<StationResponseDto> findAll() {
        return stationService.findAll();
    }

    @GetMapping("/by-name")
    public StationResponseDto getStation(@RequestParam String name) {
        Assert.hasText(name, "name 값은 필수입니다.");
        return stationService.getStation(name);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleException(IllegalArgumentException e) {
        return e.getMessage();
    }

}
