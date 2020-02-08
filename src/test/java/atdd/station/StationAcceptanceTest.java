package atdd.station;

import atdd.station.entity.StationEntity;
import atdd.station.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Transactional
public class StationAcceptanceTest {

  private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private StationRepository stationRepository;

  @Test
  @Rollback
  public void createStation() {
    //Given

    //When
    String stationName = "강남역";
    String inputJSON = "{\"name\":\"" + stationName + "\"}";

    ResponseSpec responseSpec = webTestClient.post().uri("/stations")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(inputJSON), String.class)
        .exchange();

    //Then
    responseSpec
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectHeader().valueMatches("Location", ".*/stations/[0-9]*$")
        .expectBody().jsonPath("$.name").isEqualTo(stationName);
  }

  @Test
  @Rollback
  public void getStationList() {
    //Given
    String stationName = "강남역";
    StationEntity mockStation = new StationEntity(stationName);
    stationRepository.save(mockStation);

    //When
    ResponseSpec responseSpec = webTestClient
        .get()
        .uri("/stations")
        .exchange();

    //Then
    responseSpec
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody().jsonPath("$.stations[0].name").isEqualTo(stationName);
  }

  @Test
  @Rollback
  public void getStationInfo() {
    //Given
    String stationName = "강남역";
    StationEntity mockStation = new StationEntity(stationName);
    stationRepository.save(mockStation);

    //When
    ResponseSpec responseSpec = webTestClient
        .get()
        .uri("/stations/" + stationName)
        .exchange();

    //Then
    responseSpec
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody().jsonPath("$.name").isEqualTo(stationName);
  }

  @Test
  @Rollback
  public void deleteStation() {
    //Given
    String stationName = "강남역";
    StationEntity mockStation = new StationEntity(stationName);
    stationRepository.save(mockStation);

    //When
    ResponseSpec responseSpec = webTestClient
        .delete()
        .uri("/stations/" + stationName)
        .exchange();

    //Then
    responseSpec.expectStatus().isNoContent();
  }
}
