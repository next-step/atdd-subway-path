package atdd.station;

import atdd.station.usecase.StationListDTO;
import atdd.station.usecase.StationUseCase;
import atdd.station.usecase.StationDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import javax.xml.ws.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
public class StationController {

  @Autowired
  StationUseCase stationService;

  @PostMapping("/stations")
  public ResponseEntity<StationDTO> addStation(@RequestBody @Valid StationDTO stationDTO) {
    StationDTO station = stationService.addStation(stationDTO);
    return ResponseEntity.created(
        ServletUriComponentsBuilder
            .fromCurrentServletMapping()
            .path("/stations/{id}")
            .build()
            .expand(station.getId())
            .toUri()
    ).body(station);
  }

  @GetMapping("/stations")
  public ResponseEntity<StationListDTO> getAllStation() {
    StationListDTO stationListDTO = stationService.getAllStation();

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(stationListDTO);
  }

  @GetMapping("/stations/{stationID}")
  public ResponseEntity<StationDTO> getStationInfo(@PathVariable("stationID") Long stationID) {
    StationDTO station = stationService.getStation(stationID);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(station);
  }

  @DeleteMapping("/stations/{stationID}")
  public ResponseEntity removeStation(@PathVariable("stationID") Long stationID) {
    stationService.removeStation(stationID);
    return ResponseEntity.noContent()
        .build();
  }

}
