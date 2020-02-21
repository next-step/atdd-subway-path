package atdd.line;

import atdd.HttpTestSupport;
import atdd.line.controller.LineController;
import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import atdd.station.dto.SectionCreateRequestDto;
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

    public Long createLineAndGetId(LineCreateRequestDto requestDto) {
        return createLine(requestDto).getId();
    }

    public List<LineResponseDto> findAll() {
        return findAll(ROOT_URI, LineResponseDto.class);
    }

    public void addStation(Long lineId, Long stationId) {
        final String uri = UriComponentsBuilder.fromUriString(LineController.ROOT_URI + "/{lineId}/stations/{stationId}")
                .build(lineId, stationId)
                .toString();
        put(uri, Void.class);
    }

    public void addSection(Long lineId, Long stationId, SectionCreateRequestDto requestDto) {
        final String uri = UriComponentsBuilder.fromUriString(LineController.ROOT_URI + "/{lineId}/stations/{stationId}/sections")
                .build(lineId, stationId)
                .toString();

        put(uri, requestDto, SectionCreateRequestDto.class, Void.class);
    }

    public LineResponseDto getLine(Long lineId) {
        final String uri = UriComponentsBuilder.fromUriString(LineController.ROOT_URI + "/{lineId}")
                .build(lineId)
                .toString();

        return get(uri, LineResponseDto.class);
    }

}
