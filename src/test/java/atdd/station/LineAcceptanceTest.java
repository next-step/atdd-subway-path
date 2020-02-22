package atdd.station;

import atdd.line.Line;
import atdd.line.LineDto;
import atdd.station.support.EdgeHttpSupport;
import atdd.station.support.LineHttpSupport;
import atdd.station.support.StationHttpSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AbstractWebTestClientTest {

    @Test
    void create() {
        //expect
        LineHttpSupport.create(webTestClient);
    }

    @Test
    void delete() {

        final String path = LineHttpSupport.create(webTestClient).getResponseHeaders().getLocation().getPath();

        //expect
        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void show() {
        //given
        EntityExchangeResult<Station> sourceStation = StationHttpSupport.create(webTestClient, "강남역");
        EntityExchangeResult<Station> targetStation = StationHttpSupport.create(webTestClient, "역삼역");

        EntityExchangeResult<Line> line = LineHttpSupport.create(webTestClient);
        final String path = line.getResponseHeaders().getLocation().getPath();

        EdgeHttpSupport.createEdge(webTestClient, line.getResponseBody().getId(), sourceStation.getResponseBody().getId(),
                targetStation.getResponseBody().getId());

        //expect
        EntityExchangeResult<LineDto.Response> result = webTestClient.get().uri(path)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LineDto.Response.class)
                .returnResult();

        List<String> nameList = result.getResponseBody().getStations()
                .stream()
                .map(StationDto.Response::getName)
                .collect(Collectors.toList());

        assertThat(nameList).contains("강남역");
        assertThat(nameList).contains("역삼역");
    }
}
