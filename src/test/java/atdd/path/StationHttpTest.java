package atdd.path;

import atdd.domain.Station;
import atdd.dto.StationResponseView;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.stream.Collectors;

public class StationHttpTest {
    private WebTestClient webTestClient;

    public StationHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public Long createStation(String name) {
        Station station = Station.builder()
                .name(name)
                .build();

        return webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(station)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(StationResponseView.class)
                .getResponseBody()
                .toStream()
                .map(StationResponseView::getId)
                .collect(Collectors.toList())
                .get(0);
    }

    public Station findById(Long stationId) {
        StationResponseView responseView = webTestClient.get().uri("/stations/" + stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .returnResult(StationResponseView.class)
                .getResponseBody()
                .toStream()
                .collect(Collectors.toList())
                .get(0);

        return Station.of(responseView);
    }
}
