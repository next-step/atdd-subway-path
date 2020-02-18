package atdd.line;

import atdd.HttpTestSupport;
import atdd.line.controller.LineController;
import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static atdd.line.controller.LineController.ROOT_URI;

public class LineHttpTestSupport extends HttpTestSupport {

    public LineHttpTestSupport(WebTestClient webTestClient) {
        super(webTestClient);
    }

    public LineResponseDto createLine(LineCreateRequestDto requestDto) {
        return post(ROOT_URI, requestDto, LineCreateRequestDto.class, LineResponseDto.class);
    }

    public List<LineResponseDto> findAll() {
        return findAll(ROOT_URI, LineResponseDto.class);
    }

    public void addStation(Long lindId, Long stationId) {
        final String uri = UriComponentsBuilder.fromUriString(LineController.ROOT_URI + "/{lineId}/stations/{stationId}")
                .build(lindId, stationId)
                .toString();
        put(uri, Void.class);
    }

}
