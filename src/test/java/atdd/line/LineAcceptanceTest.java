package atdd.line;

import atdd.station.StationAcceptanceTest;
import atdd.station.StationResponse;
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

import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
public class LineAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("노선을 등록한다.")
    @Test
    public void createLine(){
       String STATION_NAME = "2호선";
       LocalTime START_TIME = LocalTime.of(5,0);
       LocalTime END_TIME = LocalTime.of(23, 30);
       int INTERVAL_TIME = 10;
       int EXTRA_FARE = 0;

        Line line = Line.builder()
                .name(STATION_NAME)
                .start_time(START_TIME)
                .end_time(END_TIME)
                .interval_time(INTERVAL_TIME)
                .extra_fare(EXTRA_FARE)
                .build();

        EntityExchangeResult<LineResponse> result = webTestClient.post().uri("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(line), Line.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(LineResponse.class)
                .returnResult()
                ;

        String location = result.getResponseHeaders().getLocation().getPath();
        LineResponse resultLine = result.getResponseBody();

        assertThat(location).isEqualTo("/lines/"+ resultLine.getId());
        assertThat(resultLine.getName()).isEqualTo(STATION_NAME);
        assertThat(resultLine.getStart_time()).isEqualTo(START_TIME);
        assertThat(resultLine.getEnd_time()).isEqualTo(END_TIME);
        assertThat(resultLine.getInterval_time()).isEqualTo(INTERVAL_TIME);
        assertThat(resultLine.getExtra_fare()).isEqualTo(EXTRA_FARE);

    }

    @DisplayName("중복되는 create 테스트 코드")
    private LineResponse create(){
        String STATION_NAME = "2호선";
        LocalTime START_TIME = LocalTime.of(5,0);
        LocalTime END_TIME = LocalTime.of(23, 30);
        int INTERVAL_TIME = 10;
        int EXTRA_FARE = 0;

        Line line = Line.builder()
                .name(STATION_NAME)
                .start_time(START_TIME)
                .end_time(END_TIME)
                .interval_time(INTERVAL_TIME)
                .extra_fare(EXTRA_FARE)
                .build();
        LineResponse result = webTestClient.post().uri("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(line), Line.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(LineResponse.class)
                .returnResult()
                .getResponseBody()
                ;
        return result;
    }

}
