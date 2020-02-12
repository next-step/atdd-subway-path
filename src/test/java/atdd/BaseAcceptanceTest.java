package atdd;

import atdd.line.api.response.LineResponseView;
import atdd.line.domain.LineRepository;
import atdd.station.api.response.StationResponseView;
import atdd.station.domain.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public abstract class BaseAcceptanceTest {

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    protected EntityExchangeResult<StationResponseView> createStation(String inputJson) {
        return requestCreatePost(StationResponseView.class, "/stations", inputJson);
    }

    protected EntityExchangeResult<LineResponseView> createLine(String inputJson) {
        return requestCreatePost(LineResponseView.class, "/lines", inputJson);
    }

    protected void deleteAll() {
        stationRepository.deleteAll();
        lineRepository.deleteAll();
    }

    private <T> EntityExchangeResult<T> requestCreatePost(Class<T> body, String uri, String inputJson) {
        return webTestClient.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(body)
                .returnResult();
    }

}
