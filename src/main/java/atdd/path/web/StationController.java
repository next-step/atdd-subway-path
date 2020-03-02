package atdd.path.web;

import atdd.path.application.dto.StationRequestView;
import atdd.path.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stations")
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody StationRequestView requestView){
        return ResponseEntity
                .ok()
                .build();
    }
}
