package atdd.Edge;

import atdd.station.StationAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
class EdgeAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("구간을 등록한다")
    @Test
    void createEdge() {
        Long LINE_ID = 1L;
        int ELAPSED_TIME = 5;
        BigDecimal DISTANCE = new BigDecimal("1.1");
        Long SOURCE_STATION_ID = 1L;
        Long TARGET_SATION_ID = 2L;

        Edge edge = Edge.builder()
                .lineId(LINE_ID)
                .elapsedTime(ELAPSED_TIME)
                .distance(DISTANCE)
                .sourceStationId(SOURCE_STATION_ID)
                .targetStationId(TARGET_SATION_ID)
                .build();

        EntityExchangeResult<EdgeResponse> result = webTestClient.post().uri("/edge")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(edge), Edge.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(EdgeResponse.class)
                .returnResult()
                ;

        String location = result.getResponseHeaders().getLocation().getPath();
        EdgeResponse resultStation = result.getResponseBody();

        assertThat(location).isEqualTo("/edge/"+resultStation.getId());
        assertThat(resultStation.getLineId()).isEqualTo(LINE_ID);

    }

    @DisplayName("중복")
    public void createEdge(Long lineId, Long sourceStationId, Long targetStationId){

        int ELAPSED_TIME = 5;
        BigDecimal DISTANCE = new BigDecimal("1.1");

        Edge edge = Edge.builder()
                .lineId(lineId)
                .elapsedTime(ELAPSED_TIME)
                .distance(DISTANCE)
                .sourceStationId(sourceStationId)
                .targetStationId(targetStationId)
                .build();

        webTestClient.post().uri("/edge")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(edge), Edge.class)
                .exchange();

    }
}