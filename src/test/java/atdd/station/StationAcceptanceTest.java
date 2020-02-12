/*
 *
 * StationAcceptanceTest
 *
 * 0.0.1
 *
 * Copyright 2020 irrationnelle <drakkarverenis@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * */
package atdd.station;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testCreateReadDeleteStation() {
        Station targetStation = new Station(1L, "강남역");
        String prefixUri = "/stations";
        String inputJson = String.format("{\"name\": \"%s\"}", targetStation.getName());

        // create station test
        String createStationUri = prefixUri;


        createRequestWebTestClient(createStationUri, inputJson).expectBody(Station.class)
                                                               .consumeWith(result -> {
                                                                   Station station = result.getResponseBody();
                                                                   HttpHeaders responseHeaders =
                                                                           result.getResponseHeaders();
                                                                   URI location = responseHeaders.getLocation();
                                                                   String stringifyLocation = location.toString();
                                                                   assertThat(stringifyLocation).isEqualTo(String.format("%s/%d", createStationUri, station.getId()));
                                                               });


        // read stations test
        String readStationsUri = prefixUri;

        readRequestWebTestClient(readStationsUri).isOk()
                                                 .expectHeader()
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .expectBodyList(Station.class)
                                                 .hasSize(1)
                                                 .consumeWith(result -> {
                                                     List<Station> stations = result.getResponseBody();
                                                     Station station = stations.get(0);

                                                     assertThat(station.getName()).isEqualTo("강남역");
                                                 });


        // read station test
        String readOrDeleteStationUri = getRequestUri(prefixUri, targetStation.getId());

        readRequestWebTestClient(readOrDeleteStationUri).isOk()
                                                        .expectHeader()
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .expectBody(Station.class)
                                                        .consumeWith(result -> {
                                                            Station station = result.getResponseBody();
                                                            assertThat(station.getName()).isEqualTo("강남역");
                                                        });

        String incorrectReadStationUri = getRequestUri(prefixUri, 93);

        readRequestWebTestClient(incorrectReadStationUri).isNoContent()
                                                         .expectBody(Void.class);


        // delete station test
        webTestClient.delete()
                     .uri(readOrDeleteStationUri)
                     .exchange()
                     .expectStatus()
                     .isOk();

        readRequestWebTestClient(readOrDeleteStationUri).isNoContent()
                                                        .expectBody(Void.class);

    }

    @Test
    public void testCreateReadStationLine() {
        String createAndReadLinesUri = "/lines";
        Line targetLine = new Line(1L, "2호선", "05:00", "23:00", 10);
        String inputJson = String.format("{\"name\": \"%s\", \"startTime\": \"%s\", \"endTime\": \"%s\", " +
                "\"stationInterval\": \"%d\"}", targetLine.getName(), targetLine.getStartTime(),
                targetLine.getEndTime(), targetLine.getStationInterval());

        createRequestWebTestClient(createAndReadLinesUri, inputJson).expectBody(Line.class)
                                                                    .consumeWith(result -> {
                                                                        HttpHeaders responseHeaders =
                                                                                result.getResponseHeaders();
                                                                        URI location = responseHeaders.getLocation();
                                                                        String stringifyLocation = location.toString();
                                                                        assertThat(stringifyLocation).isEqualTo(String.format("%s/%d", createAndReadLinesUri, 1));
                                                                    });

        readRequestWebTestClient(createAndReadLinesUri).isOk()
                                                       .expectHeader()
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .expectBodyList(Line.class)
                                                       .hasSize(1)
                                                       .consumeWith(result -> {
                                                           List<Line> lines = result.getResponseBody();
                                                           Line line = lines.get(0);

                                                           assertThat(line.getName()).isEqualTo("2호선");
                                                           assertThat(line.getStartTime()).isEqualTo("05:00");
                                                           assertThat(line.getStationInterval()).isEqualTo(10);
                                                       });


    }

    private StatusAssertions readRequestWebTestClient(String uri) {
        return webTestClient.get()
                            .uri(uri)
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus();
    }

    private WebTestClient.ResponseSpec createRequestWebTestClient(String requestUri, String inputJson) {
        return webTestClient.post()
                            .uri(requestUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Mono.just(inputJson), String.class)
                            .exchange()
                            .expectStatus()
                            .isCreated()
                            .expectHeader()
                            .exists("Location")
                            .expectHeader()
                            .contentType(MediaType.APPLICATION_JSON);


    }

    private String getRequestUri(String prefixUri, long entityId) {
        return String.format("%s/%d", prefixUri, entityId);
    }
}
