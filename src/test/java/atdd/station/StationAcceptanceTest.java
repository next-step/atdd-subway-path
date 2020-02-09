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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);
    private final Long id = new Long(1);
    private final Station targetStation = new Station(id, "강남역");
    private final String prefixUri = "/stations";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testCreateStation() {
        String inputJson = String.format("{\"name\": \"%s\"}", targetStation.getName());
        String reqUri = prefixUri;

        webTestClient.post()
                     .uri(reqUri)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(inputJson), String.class)
                     .exchange()
                     .expectStatus()
                     .isCreated();
    }

    @Test
    public void testReadStations() {
        testCreateStation();

        String reqUri = prefixUri;

        readRequestWebTestClient(reqUri)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Station.class)
                .hasSize(1)
                .consumeWith(result -> {
                    List<Station> stations = result.getResponseBody();
                    Station station = stations.get(0);

                    Assertions.assertThat(station.getName())
                              .isEqualTo("강남역");
                });
    }

    @Test
    public void testReadStation() {
        testCreateStation();

        String reqUri = String.format("%s/%d", prefixUri, targetStation.getId());

        readRequestWebTestClient(reqUri)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(Station.class)
                .consumeWith(result -> {
                    Station station = result.getResponseBody();
                    Assertions.assertThat(station.getName()).isEqualTo("강남역");
                });
    }

    private WebTestClient.ResponseSpec readRequestWebTestClient(String uri) {
        return webTestClient.get()
                            .uri(uri)
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus()
                            .isOk();
    }
}
