package atdd.station;

import atdd.station.controller.StationController;
import atdd.station.dto.StationResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {

    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("지하철역 등록")
    @Test
    void create() throws Exception {
        final String uri = StationController.ROOT_URI;
        final String stationName = "강남역";
        final String inputJson = "{\"name\":\"" + stationName + "\"}";

        final EntityExchangeResult<StationResponseDto> result = webTestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(StationResponseDto.class)
                .returnResult();

        final String location = result.getResponseHeaders().getLocation().getPath();
        final StationResponseDto responseView = result.getResponseBody();

        assertThat(location).isEqualTo(uri + "/" + responseView.getId());
        assertThat(responseView.getName()).isEqualTo(stationName);
    }

    @DisplayName("지하철역 목록 조회")
    @Test
    void findAll() throws Exception {
        final String uri = StationController.ROOT_URI;
        final String stationName = "강남역";
        final String inputJson = "{\"name\":\"" + stationName + "\"}";

        webTestClient.post()
                .uri(StationController.ROOT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange();


        final List<StationResponseDto> results = webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StationResponseDto.class)
                .returnResult()
                .getResponseBody();


        final boolean contains = results.stream().anyMatch(response -> stationName.equals(response.getName()));
        assertThat(contains).isTrue();
    }


}
