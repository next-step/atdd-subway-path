package atdd.station;

import atdd.HttpTestSupport;
import atdd.station.dto.StationCreateRequestDto;
import atdd.station.dto.StationResponseDto;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static atdd.station.controller.StationController.ROOT_URI;

public class StationHttpTestSupport extends HttpTestSupport {

    public StationHttpTestSupport(WebTestClient webTestClient) {
        super(webTestClient);
    }

    public StationResponseDto createStation(StationCreateRequestDto requestDto) {
        return post(ROOT_URI, requestDto, StationCreateRequestDto.class, StationResponseDto.class);
    }

    public Long createStationAndGetId(StationCreateRequestDto requestDto) {
        return createStation(requestDto).getId();
    }

    public List<StationResponseDto> findAll() {
        return findAll(ROOT_URI, StationResponseDto.class);
    }

    public StationResponseDto getStation(Long stationId) {
        return get(ROOT_URI + "/" + stationId, StationResponseDto.class);
    }

}
