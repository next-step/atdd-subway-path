package atdd.station;

import atdd.station.model.CreateStationRequestView;
import atdd.station.model.dto.StationDto;
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

    public StationDto createStation(String name) {
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
                .expectBody(StationDto.class).returnResult();

        return (StationDto) result.getResponseBody();
    }

    public List<StationDto> findAll() {
        EntityExchangeResult result = webTestClient.get().uri(STATIONS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<StationDto>>() {
                }).returnResult();

        return (List<StationDto>) result.getResponseBody();
    }

    public StationDto findById(final long stationId) {
        EntityExchangeResult result = webTestClient.get().uri(STATIONS_PATH + "/" + stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StationDto.class).returnResult();

        return (StationDto) result.getResponseBody();
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
