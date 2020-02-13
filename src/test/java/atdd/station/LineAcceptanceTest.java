package atdd.station;

import atdd.station.entity.Station;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Mono;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class LineAcceptanceTest {
  private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void createLine() {
    //Given

    //When
    String lineName = "2호선";
    String startTime = "5:00";
    String endTime = "23:50";
    String intervalTime = "10";
    String inputJSON = "{\"name\":\"" + lineName
        + "\",\"startTime\":\"" + startTime
        + "\",\"endTime\":\"" + endTime
        + "\",\"intervalTime\":\"" + intervalTime + "\"}";

    ResponseSpec responseSpec = webTestClient.post().uri("/lines")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(inputJSON), String.class)
        .exchange();

    //Then
    responseSpec
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectHeader().valueMatches("Location", ".*/lines/[0-9]*$")
        .expectBody().jsonPath("$.name").isEqualTo(lineName)
        .jsonPath("$.startTime").isEqualTo(startTime)
        .jsonPath("$.endTime").isEqualTo(endTime)
        .jsonPath("$.intervalTime").isEqualTo(intervalTime);

  }
}
