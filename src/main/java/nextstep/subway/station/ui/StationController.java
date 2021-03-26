package nextstep.subway.station.ui;

import nextstep.subway.auth.domain.AuthenticationPrincipal;
import nextstep.subway.member.domain.LoginMember;
import nextstep.subway.station.application.StationDuplicateException;
import nextstep.subway.station.application.StationNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestController
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@AuthenticationPrincipal LoginMember loginMember, @RequestBody StationRequest stationRequest) {
        StationResponse station = stationService.saveStation(loginMember, stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations(@AuthenticationPrincipal LoginMember loginMember) {
        return ResponseEntity.ok().body(stationService.findAllStations(loginMember));
    }

    @PutMapping("/stations/{id}")
    public ResponseEntity updateStation(@AuthenticationPrincipal LoginMember loginMember, @PathVariable Long id, @RequestBody StationRequest stationRequest) {
        stationService.updateStation(loginMember, id, stationRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@AuthenticationPrincipal LoginMember loginMember, @PathVariable Long id) {
        stationService.deleteStationById(loginMember, id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(StationDuplicateException.class)
    public ResponseEntity handleStationDuplicateException(StationDuplicateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<String> handleStationNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<String> handleSQLIntegrityConstraintViolationException() {
        return ResponseEntity.badRequest().body("노선에 등록된 역은 삭제할 수 없습니다.");
    }
}
