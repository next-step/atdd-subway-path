package atdd.station;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class StationHttpSupport {

    public static EntityExchangeResult<Station> create(WebTestClient webTestClient, String name) {

        final String inputJson = "{\"name\":\"" + name + "\"}";

        return webTestClient.post().uri(StationUri.ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Station.class)
                .returnResult();
    }

}
