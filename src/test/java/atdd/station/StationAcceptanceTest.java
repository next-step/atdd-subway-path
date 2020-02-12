package atdd.station;

import atdd.AcceptanceTestSupport;
import atdd.station.controller.StationController;
import atdd.station.dto.StationResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends AcceptanceTestSupport {

    @DisplayName("지하철역 등록")
    @Test
    void create() {
        final String uri = StationController.ROOT_URI;
        final String stationName = "강남역";
        final Map<String, String> request = Collections.singletonMap("name", stationName);

        final EntityExchangeResult<StationResponseDto> result = webTestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), Map.class)
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
        final String stationName = "강남역";
        final Map<String, String> request = Collections.singletonMap("name", stationName);
        create(StationController.ROOT_URI, request, StationResponseDto.class);


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
        final String stationName = "강남역";
        final Map<String, String> request = Collections.singletonMap("name", stationName);
        final StationResponseDto responseDto = create(StationController.ROOT_URI, request, StationResponseDto.class);

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
        final String stationName = "강남역";
        final Map<String, String> request = Collections.singletonMap("name", stationName);
        final StationResponseDto responseDto = create(StationController.ROOT_URI, request, StationResponseDto.class);

        webTestClient.delete()
                .uri(StationController.ROOT_URI + "/" + responseDto.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

}
