package atdd.station;

import atdd.station.model.Station;
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

import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createStation(){
        String stationName = "강남역";
        String inputJson = "{\"name\":\"" + stationName + "\"}";

        EntityExchangeResult result = webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(Station.class).returnResult();

        String location = result.getResponseHeaders().getLocation().getPath();

        logger.info("createStation location = {}", location);
    }

    @Test
    public void findAllStations(){
        EntityExchangeResult result = webTestClient.get().uri("/stations")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(List.class).returnResult();

        logger.info("findAllStations = {}", result.getResponseBody().toString());
    }

    @Test
    public void findStation(){
        long stationId = 1;

        EntityExchangeResult result = webTestClient.get().uri("/stations/" + stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Station.class).returnResult();

        logger.info("findStation = {}", result.getResponseBody().toString());
    }
}
