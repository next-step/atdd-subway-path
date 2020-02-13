package atdd.station;

import atdd.station.model.CreateStationRequestView;
import atdd.station.model.Station;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createStation() {
        //when
        final String stationName = "강남역";

        String inputJson = writeValueAsString(CreateStationRequestView.builder()
                .name(stationName)
                .build());

        EntityExchangeResult result = webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(Station.class).returnResult();

        // then
        String location = result.getResponseHeaders().getLocation().getPath();

        Station station = (Station) result.getResponseBody();
        Station actualStation = Station.builder()
                .id(station.getId())
                .name(stationName)
                .build();

        String expected = writeValueAsString(station);
        String actual = writeValueAsString(actualStation);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void findAllStations() {
        // given
        List<Station> stations = createStations();

        // when
        EntityExchangeResult result = webTestClient.get().uri("/stations")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(List.class).returnResult();

        //then
        String expected = writeValueAsString(result.getResponseBody());
        String actual = writeValueAsString(stations);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void findStation() {
        // given
        List<Station> stations = createStations();

        // when
        long stationId = 1;

        EntityExchangeResult result = webTestClient.get().uri("/stations/" + stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Station.class).returnResult();

        // then
        String expected = writeValueAsString(result.getResponseBody());
        String actual = writeValueAsString(stations.stream().filter(data -> data.getId() == stationId).findAny().get());

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void deleteStation() {
        // given
        createStations();

        // when
        long stationId = 1;

        EntityExchangeResult result = webTestClient.delete().uri("/stations/" + stationId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().returnResult();

        // then
        Optional<Station> optionalStation = findById(stationId);
        Station station = optionalStation.isPresent() ? optionalStation.get() : null;

        assertThat(station).isNull();
    }

    private List<Station> createStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(createStation("강남역"));
        stations.add(createStation("삼성역"));

        return stations;
    }

    private Station createStation(String name) {
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

    private Optional<Station> findById(final long stationId) {
        return Optional.ofNullable(
                webTestClient.get()
                        .uri("/stations/" + stationId)
                        .exchange()
                        .expectBody(Station.class)
                        .returnResult()
                        .getResponseBody());
    }

    private String writeValueAsString(Object object) {
        String result = null;
        try {
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
