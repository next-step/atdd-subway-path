package atdd.station;

import atdd.station.model.Station;
import atdd.station.repository.StationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private StationRepository repository;

    @Test
    public void createStation(){
        //when
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

        // then
        String location = result.getResponseHeaders().getLocation().getPath();

        Station station = (Station) result.getResponseBody();

        String expected = writeValueAsString(station);
        String actual = writeValueAsString(repository.findById(station.getId()));

        Assert.assertEquals(expected, actual);

        logger.info("createStation location = {}", location);
    }

    @Test
    public void findAllStations(){
        // given
        createStations();

        // when
        EntityExchangeResult result = webTestClient.get().uri("/stations")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(List.class).returnResult();

        //then
        String expected = writeValueAsString(result.getResponseBody());
        String actual = writeValueAsString(repository.findAll());

        Assert.assertEquals(expected, actual);

        logger.info("findAllStations = {}", expected);
    }

    @Test
    public void findStation(){
        // given
        createStations();

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
        String actual = writeValueAsString(repository.findById(stationId));

        Assert.assertEquals(expected, actual);

        logger.info("findStation = {}", expected);
    }

    @Test
    public void deleteStation(){
        // given
        createStations();

        // when
        long stationId = 1;

        EntityExchangeResult result = webTestClient.delete().uri("/stations/" + stationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().returnResult();

        // then
        Station station = repository.findById(stationId);

        Assert.assertEquals(null, station);

        logger.info("deleteStation = {}", station);
    }

    private void createStations() {
        createStation("강남역");
        createStation("삼성역");
    }

    private void createStation(String name) {

        String inputJson = "{\"name\":\"" + name + "\"}";

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange();
    }

    private String writeValueAsString(Object object) {
        String result = null;
        try {
            result = mapper.writeValueAsString(object);
        }catch (JsonProcessingException e){
            logger.error("JsonProcessingException", e);
        }

        return result;
    }
}
