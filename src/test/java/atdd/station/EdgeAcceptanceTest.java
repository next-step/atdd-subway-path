package atdd.station;

import atdd.station.support.EdgeHttpSupport;
import atdd.station.support.LineHttpSupport;
import atdd.station.support.StationHttpSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EdgeAcceptanceTest extends AbstractWebTestClientTest {

    @BeforeEach
    void setUp() {
        LineHttpSupport.create(webTestClient);
        StationHttpSupport.create(webTestClient, "석계역");
        StationHttpSupport.create(webTestClient, "서울역");
        StationHttpSupport.create(webTestClient, "회현");
    }

    @Test
    void create() {

        //expect
        EdgeHttpSupport.createEdge(webTestClient);
    }

    @Test
    void delete() {

        final String path = EdgeHttpSupport.createEdge(webTestClient).getResponseHeaders().getLocation().getPath();

        //expect
        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNotFound();
    }

}
