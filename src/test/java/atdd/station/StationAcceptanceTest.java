package atdd.station;

import atdd.AcceptanceTestSupport;
import atdd.station.controller.StationController;
import atdd.station.dto.StationCreateRequestDto;
import atdd.station.dto.StationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends AcceptanceTestSupport {

    final String stationName = "강남역";

    private StationHttpTestSupport stationHttpTestSupport;

    @BeforeEach
    void setup() {
        this.stationHttpTestSupport = new StationHttpTestSupport(webTestClient);
    }

    @DisplayName("지하철역 등록")
    @Test
    void create() {
        final String uri = StationController.ROOT_URI;
        final StationCreateRequestDto requestDto = StationCreateRequestDto.of(stationName);

        final EntityExchangeResult<StationResponseDto> result = webTestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), StationCreateRequestDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(StationResponseDto.class)
                .returnResult();

        final String location = result.getResponseHeaders().getLocation().getPath();
        final StationResponseDto responseView = result.getResponseBody();

        assertThat(location).isEqualTo(uri + "/" + responseView.getId());
        assertThat(responseView.getName()).isEqualTo(stationName);
    }

    @DisplayName("지하철역 목록 조회")
    @Test
    void findAll() {
        stationHttpTestSupport.createStation(StationCreateRequestDto.of(stationName));


        final List<StationResponseDto> results = webTestClient.get()
                .uri(StationController.ROOT_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StationResponseDto.class)
                .returnResult()
                .getResponseBody();


        final boolean contains = results.stream().anyMatch(response -> stationName.equals(response.getName()));
        assertThat(contains).isTrue();
    }

    @DisplayName("지하철역 정보 조회")
    @Test
    void getStation() {
        final StationResponseDto responseDto = stationHttpTestSupport.createStation(StationCreateRequestDto.of(stationName));

        final StationResponseDto stationResponseDto = webTestClient.get()
                .uri(StationController.ROOT_URI + "/" + responseDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(StationResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(stationResponseDto).isNotNull();
        assertThat(stationResponseDto.getName()).isEqualTo(stationName);
    }

    @DisplayName("지하철역 지하철역 삭제")
    @Test
    void delete() {
        final StationResponseDto responseDto = stationHttpTestSupport.createStation(StationCreateRequestDto.of(stationName));

        webTestClient.delete()
                .uri(StationController.ROOT_URI + "/" + responseDto.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

}
