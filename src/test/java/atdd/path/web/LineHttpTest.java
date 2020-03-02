package atdd.path.web;

import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class LineHttpTest {
    public static final String LINE_BASE_URI = "/lines";
    public WebTestClient webTestClient;

    public LineHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Autowired
    private ObjectMapper objectMapper;

    public LineResponseView create(String lineName) throws Exception {
        LineRequestView requestView = new LineRequestView();
        String inputJson = objectMapper.writeValueAsString(requestView);

        return webTestClient.post().uri(LINE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(LineResponseView.class)
                .returnResult()
                .getResponseBody();
    }
}
