package atdd.path;

import atdd.AbstractAcceptanceTest;
import atdd.path.dto.StationRequestView;
import atdd.path.dto.StationResponseView;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

public class StationHttpTest extends AbstractAcceptanceTest {
    public WebTestClient webTestClient;

    public StationHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public Long create(String stationName) {
        StationRequestView requestView = new StationRequestView(stationName);
        EntityExchangeResult<StationResponseView> result
                = webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestView)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(StationResponseView.class)
                .returnResult();

        return result.getResponseBody().getId();
    }

    public StationResponseView findById(Long stationId) {
        EntityExchangeResult<StationResponseView> result = webTestClient.get().uri("/stations/" + stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StationResponseView.class)
                .returnResult();

        return result.getResponseBody();
    }
}
