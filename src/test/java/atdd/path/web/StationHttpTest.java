package atdd.path.web;

import atdd.path.application.dto.StationResponseView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    public StationResponseView create(String stationName) {
        String inputJson = "{\"name\":\"" + stationName + "\"}";

        return webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(StationResponseView.class)
                .returnResult()
                .getResponseBody();
    }

    public EntityExchangeResult<StationResponseView> retrieveStationRequest(String uri) {
        return webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StationResponseView.class)
                .returnResult();
    }

    public List<StationResponseView> showStationsRequest() {
        return webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(StationResponseView.class)
                .returnResult()
                .getResponseBody();
    }

    public EntityExchangeResult<StationResponseView> retrieveStation(Long stationId) {
        return retrieveStationRequest("/stations/" + stationId);
    }
}
