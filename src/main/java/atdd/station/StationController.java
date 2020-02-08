package atdd.station;

import atdd.station.entity.StationEntity;
import atdd.station.service.IStationService;
import atdd.station.service.StationDTO;
import atdd.station.service.StationService;
import java.net.URI;
import javax.servlet.http.HttpServlet;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
public class StationController {
    @Autowired
    IStationService stationService;

    @PostMapping("/stations")
    public ResponseEntity<StationDTO> addStation(@RequestBody @Valid StationDTO stationDTO){
        StationDTO station = stationService.addStation(stationDTO);
        return ResponseEntity.created(
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri()
        ).body(station);
    }


}
