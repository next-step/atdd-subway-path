package nextstep.subway.line.web;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.line.service.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationsDto;
import nextstep.subway.station.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final StationService stationService;

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        StationsDto stationsDto = new StationsDto(upStation, downStation);

        LineResponse line = lineService.saveLine(request, stationsDto);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable("id") Long lineId) {
        return ResponseEntity.ok(lineService.findLine(lineId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> modifyLine(@PathVariable("id") Long lineId,
                                                   @RequestBody LineRequest request) {
        return ResponseEntity.ok(lineService.updateLine(lineId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable("id") Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<SectionResponse> addSection(@RequestBody SectionRequest request,
                                                      @PathVariable("id") Long lineId) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        StationsDto stationsDto = new StationsDto(upStation, downStation);

        SectionResponse section = lineService.addSection(lineId, stationsDto, request.getDistance());
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + section.getId()))
                .body(section);
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable("id") Long lineId, HttpServletRequest request) {
        Station station = stationService.findById(Long.valueOf(request.getParameter("stationId")));

        lineService.deleteSection(lineId, station);
        return ResponseEntity.noContent().build();
    }

}
