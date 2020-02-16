package atdd.line;

import atdd.HttpTestSupport;
import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static atdd.line.controller.LineController.ROOT_URI;

public class LineHttpTestSupport extends HttpTestSupport {

    public LineHttpTestSupport(WebTestClient webTestClient) {
        super(webTestClient);
    }

    public LineResponseDto createLine(LineCreateRequestDto requestDto) {
        return create(ROOT_URI, requestDto, LineCreateRequestDto.class, LineResponseDto.class);
    }

    public List<LineResponseDto> findAll() {
        return findAll(ROOT_URI, LineResponseDto.class);
    }

}
