package atdd.path.web;

import atdd.path.application.dto.StationResponseView;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class StationHttpTest {
    public WebTestClient webTestClient;

    public StationHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public EntityExchangeResult<StationResponseView> createStationRequest(String stationName) {
        String inputJson = "{\"name\":\"" + stationName + "\"}";

        return webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(StationResponseView.class)
                .returnResult();
    }

    public EntityExchangeResult<StationResponseView> retrieveStationRequest(String uri) {
        return webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StationResponseView.class)
                .returnResult();
    }

    public EntityExchangeResult<List<StationResponseView>> showStationsRequest() {
        return webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(StationResponseView.class)
                .returnResult();
    }

    public Long createStation(String stationName) {
        EntityExchangeResult<StationResponseView> stationResponse = createStationRequest(stationName);
        return stationResponse.getResponseBody().getId();
    }

    public EntityExchangeResult<StationResponseView> retrieveStation(Long stationId) {
        return retrieveStationRequest("/stations/" + stationId);
    }
}
