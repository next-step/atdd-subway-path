package atdd.station;

import atdd.line.Line;
import atdd.line.LineLink;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class LineHttpSupport {


    public static EntityExchangeResult<Line> create(WebTestClient webTestClient) {

        final String inputJson = "{\"name\":\"1호선\",\"startTime\":\"05:30\",\"endTime\":\"23:30\",\"intervalTime\":5}";

        return webTestClient.post().uri(LineLink.ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Line.class)
                .returnResult();
    }
}
