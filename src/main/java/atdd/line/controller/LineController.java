package atdd.line.controller;

import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import atdd.line.service.LineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(LineController.ROOT_URI)
public class LineController {

    public static final String ROOT_URI = "/lines";

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponseDto> create(@RequestBody @Valid LineCreateRequestDto requestDto) {

        LineResponseDto responseDto = lineService.create(requestDto);

        return ResponseEntity.created(URI.create(ROOT_URI + "/" + responseDto.getId())).body(responseDto);
    }

}
