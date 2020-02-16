package atdd.station;

import atdd.station.model.CreateStationRequestView;
import atdd.station.model.entity.Station;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class StationTestUtils {
    private final String STATIONS_PATH = "/stations";

    private static final ObjectMapper mapper = new ObjectMapper();
    public WebTestClient webTestClient;

    public StationTestUtils(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public Station createStation(String name) {
        String inputJson = writeValueAsString(CreateStationRequestView.builder()
                .name(name)
                .build());

        EntityExchangeResult result = webTestClient.post().uri(STATIONS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(Station.class).returnResult();

        return (Station) result.getResponseBody();
    }

    public List<Station> findAll() {
        EntityExchangeResult result = webTestClient.get().uri(STATIONS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<Station>>() {
                }).returnResult();

        return (List<Station>) result.getResponseBody();
    }

    public Station findById(final long stationId) {
        EntityExchangeResult result = webTestClient.get().uri(STATIONS_PATH + "/" + stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Station.class).returnResult();

        return (Station) result.getResponseBody();
    }

    public void deleteById(final long id) {
        EntityExchangeResult result = webTestClient.delete().uri(STATIONS_PATH + "/" + id)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().returnResult();
    }

    public String writeValueAsString(Object object) {
        String result = null;
        try {
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
