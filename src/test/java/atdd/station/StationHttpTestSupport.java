package atdd.station;

import atdd.HttpTestSupport;
import atdd.station.dto.StationCreateRequestDto;
import atdd.station.dto.StationResponseDto;
import org.springframework.test.web.reactive.server.WebTestClient;

import static atdd.station.controller.StationController.ROOT_URI;

public class StationHttpTestSupport extends HttpTestSupport {

    public StationHttpTestSupport(WebTestClient webTestClient) {
        super(webTestClient);
    }

    public StationResponseDto createStation(StationCreateRequestDto requestDto) {
        return post(ROOT_URI, requestDto, StationCreateRequestDto.class, StationResponseDto.class);
    }

}
