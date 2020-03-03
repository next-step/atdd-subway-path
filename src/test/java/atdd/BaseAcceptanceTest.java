package atdd;

import atdd.line.api.response.LineResponseView;
import atdd.line.domain.Edge;
import atdd.line.domain.EdgeRepository;
import atdd.line.domain.Line;
import atdd.line.domain.LineRepository;
import atdd.station.api.response.StationResponseView;
import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static atdd.line.controller.LineController.EDGE_URL;
import static atdd.line.controller.LineController.LINE_URL;
import static atdd.station.controller.StationController.STATION_URL;
import static atdd.util.TestUtils.jsonOf;
import static atdd.util.TestUtils.localTimeToString;

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

    @Autowired
    private EdgeRepository edgeRepository;

    protected EntityExchangeResult<StationResponseView> createStation(Station station) {
        Map<String, Object> map = Map.ofEntries(
                Map.entry("name", station.getName())
        );
        return requestCreatePost(StationResponseView.class, STATION_URL, jsonOf(map));
    }

    protected EntityExchangeResult<LineResponseView> createLine(Line line) {
        Map<String, Object> map = Map.ofEntries(
                Map.entry("name", line.getName()),
                Map.entry("startTime", localTimeToString(line.getStartTime())),
                Map.entry("endTime", localTimeToString(line.getEndTime())),
                Map.entry("intervalTime", line.getIntervalTime())
        );
        return requestCreatePost(LineResponseView.class, LINE_URL, jsonOf(map));
    }

    protected EntityExchangeResult<LineResponseView> createEdge(Long lineId, Edge edge) {
        Map<String, Object> map = Map.ofEntries(
                Map.entry("elapsedTime", edge.getElapsedTime()),
                Map.entry("distance", edge.getDistance()),
                Map.entry("sourceStationId", edge.getSourceStationId()),
                Map.entry("targetStationId", edge.getTargetStationId())
        );
        return requestCreatePost(LineResponseView.class, LINE_URL + "/" + lineId + EDGE_URL, jsonOf(map));
    }

    protected void deleteAll() {
        edgeRepository.deleteAll();
        lineRepository.deleteAll();
        stationRepository.deleteAll();
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
