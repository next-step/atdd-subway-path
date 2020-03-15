package atdd.path;

import atdd.path.dto.LineRequestView;
import atdd.path.dto.LineResponseView;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalTime;
import java.util.stream.Collectors;

public class LineHttpTest {
    private WebTestClient webTestClient;

    public LineHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public LineResponseView create(String name, LocalTime startTime, LocalTime endTIme, int intervalTime){
        LineRequestView requestView = LineRequestView.builder()
                .name(name)
                .startTime(startTime)
                .endTime(endTIme)
                .intervalTime(intervalTime)
                .build();

        return webTestClient.post().uri("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestView)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(LineResponseView.class)
                .getResponseBody()
                .toStream()
                .collect(Collectors.toList())
                .get(0);
    }

    public LineResponseView findById(Long lineId){
        return webTestClient.get().uri("/lines/"+lineId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(LineResponseView.class)
                .getResponseBody()
                .toStream()
                .collect(Collectors.toList())
                .get(0);
    }

}
