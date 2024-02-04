package nextstep.subway.line.controller;

import nextstep.subway.line.controller.dto.LineCreateRequest;
import nextstep.subway.line.controller.dto.LineResponse;
import nextstep.subway.line.controller.dto.LineUpdateRequest;
import nextstep.subway.line.service.LineService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService service;

    public LineController(final LineService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public LineResponse createLine(@RequestBody LineCreateRequest createRequestDto) {
        return service.createLine(createRequestDto);
    }

    @GetMapping
    public List<LineResponse> getLines() {
        return service.getLines();
    }

    @GetMapping("/{lineId}")
    public LineResponse getLine(@PathVariable Long lineId) {
        return service.getLine(lineId);
    }

    @PutMapping("/{lineId}")
    public void updateLine(
        @PathVariable Long lineId,
        @RequestBody LineUpdateRequest updateRequestDto
    ) {
        service.updateLine(lineId, updateRequestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{lineId}")
    public void deleteLine(@PathVariable Long lineId) {
        service.deleteLine(lineId);
    }
}
