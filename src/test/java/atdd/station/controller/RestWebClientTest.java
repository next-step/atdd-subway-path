package atdd.station.controller;

import atdd.station.domain.SubwayLine;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static atdd.station.controller.SubwayLineAcceptanceTest.SUBWAY_LINE_API_BASE_URL;

public class RestWebClientTest {
    private WebTestClient webTestClient;

    public RestWebClientTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    <T> EntityExchangeResult<T> postMethodAcceptance(String uri, Object requestBody, Class<T> bodyClass) {
        return webTestClient.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestBody), requestBody.getClass())
                .exchange()
                .expectBody(bodyClass)
                .returnResult();
    }


    <T> EntityExchangeResult<T> getMethodAcceptance(String uri, Class<T> bodyClass) {
        return this.webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(bodyClass)
                .returnResult();
    }

    <T> EntityExchangeResult<Void> deleteMethodAcceptance(String uri) {
        return this.webTestClient.delete().uri(uri)
                .exchange()
                .expectBody(Void.class)
                .returnResult();
    }

    public String creatSubwayLine(SubwayLine subwayLine) {
        return Objects.requireNonNull(this
                .postMethodAcceptance(SUBWAY_LINE_API_BASE_URL, subwayLine, SubwayLine.class)
                .getResponseHeaders()
                .getLocation())
                .getPath();
    }
}
