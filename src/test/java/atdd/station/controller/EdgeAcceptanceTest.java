package atdd.station.controller;

import atdd.station.domain.Edge;
import atdd.station.domain.Line;
import atdd.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class EdgeAcceptanceTest
{
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("지하철 구간 등록")
    public void createEdge()
    {
        webTestClient.post().uri("/edges")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(
                        Edge.builder()
                                .line(new Line("2호선"))
                                .source(new Station("강남역"))
                                .target(new Station("역삼역"))
                                .build()
                        ), Edge.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.line.name").isEqualTo("2호선")
                .jsonPath("$.source.name").isEqualTo("강남역")
                .jsonPath("$.target.name").isEqualTo("역삼역");
    }
}
