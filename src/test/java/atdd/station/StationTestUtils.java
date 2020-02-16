package atdd.station;

import atdd.station.model.CreateStationRequestView;
import atdd.station.model.entity.Station;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationTestUtils {
    private static final ObjectMapper mapper = new ObjectMapper();
    public WebTestClient webTestClient;

    public StationTestUtils(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public List<Station> createStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(createStation("강남역"));
        stations.add(createStation("역삼역"));
        stations.add(createStation("선릉역"));

        return stations;
    }

    public Station createStation(String name) {
        String inputJson = writeValueAsString(CreateStationRequestView.builder()
                .name(name)
                .build());

        EntityExchangeResult result = webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectBody(Station.class).returnResult();

        Station station = (Station) result.getResponseBody();

        return station;
    }

    public Optional<Station> findById(final long stationId) {
        return Optional.ofNullable(
                webTestClient.get()
                        .uri("/stations/" + stationId)
                        .exchange()
                        .expectBody(Station.class)
                        .returnResult()
                        .getResponseBody());
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
