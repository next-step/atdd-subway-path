package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import subway.line.dto.LineCreateRequest;
import subway.line.dto.LineResponse;
import subway.line.dto.LineUpdateRequest;
import subway.line.service.LineService;

import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LineResponse createLine(@RequestBody LineCreateRequest request) {
        return lineService.saveLine(request);
    }

    @GetMapping
    public List<LineResponse> showLines() {
        return lineService.findLines();
    }

    @GetMapping("/{id}")
    public LineResponse showLine(@PathVariable Long id) {
        return lineService.findLine(id);
    }

    @PatchMapping("/{id}")
    public void updateLine(
            @PathVariable Long id,
            @RequestBody LineUpdateRequest request
    ) {
        lineService.updateLine(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
    }
}
