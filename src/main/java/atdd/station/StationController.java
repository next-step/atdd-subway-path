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
    List<StationDTO> stations = new ArrayList<>();
    stations.add(new StationDTO((long) 1, "강남역"));
    StationListDTO mockStationListDTO = new StationListDTO(stations.size(), stations);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(mockStationListDTO);
  }

  @GetMapping("/stations/{stationName}")
  public ResponseEntity<StationDTO> getStationInfo(@PathVariable String stationName) {
    StationDTO mockStationDTO = new StationDTO((long) 1, "강남역");
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(mockStationDTO);
  }

  @DeleteMapping("/stations/{stationName}")
  public ResponseEntity removeStation(@PathVariable String stationName) {
    return ResponseEntity.noContent()
        .build();
  }

}
