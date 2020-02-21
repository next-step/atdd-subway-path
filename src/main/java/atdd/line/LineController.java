package atdd.line;


import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = LineLink.ROOT)
@RestController
public class LineController {

    private final LineService lineService;

    @PostMapping
    public ResponseEntity create(@RequestBody LineDto.Request request) {

        Line createLine = lineService.create(request.toEntity());

        return ResponseEntity.created(LineLink.getCreatedUrl(createLine.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Line line) {
        if (line == null)
            return ResponseEntity.notFound().build();
        lineService.delete(line);
        return ResponseEntity.noContent().build();
    }
}
