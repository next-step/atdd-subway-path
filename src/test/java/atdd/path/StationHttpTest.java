package atdd.path;

import atdd.AbstractAcceptanceTest;
import atdd.path.dto.StationRequestView;
import atdd.path.dto.StationResponseView;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.stream.Collectors;

public class StationHttpTest extends AbstractAcceptanceTest {
    public WebTestClient webTestClient;

    public StationHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public Long create(String stationName) {
        StationRequestView requestView = new StationRequestView("강남");
        return webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestView)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(StationResponseView.class)
                .getResponseBody()
                .toStream()
                .map(StationResponseView::getId)
                .collect(Collectors.toList())
                .get(0);
    }

    public Long findById(Long stationId){
        return webTestClient.get().uri("/stations/"+stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(StationResponseView.class)
                .getResponseBody()
                .toStream()
                .map(StationResponseView::getId)
                .collect(Collectors.toList())
                .get(0);
    }
}
