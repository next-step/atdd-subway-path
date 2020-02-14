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
import static org.assertj.core.api.Assertions.offset;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testStation() {
        Station targetStation = new Station(1L, "강남역");
        String prefixUri = "/stations";
        String inputJson = String.format("{\"name\": \"%s\"}", targetStation.getName());

        testCreateReadDelete(prefixUri, inputJson, targetStation, Station.class, targetStation.getId());
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
                            .accept(MediaType.APPLICATION_JSON)
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

    private void assertResponse(EntityExchangeResult<?> entityExchangeResult, Object targetObject) {
        Object responseBody = entityExchangeResult.getResponseBody();
        if (responseBody instanceof List) {
            List responseList = (List) responseBody;
            assertThat(responseList.get(0)).usingRecursiveComparison()
                                           .isEqualTo(targetObject);
            return;
        }
        assertThat(responseBody).usingRecursiveComparison()
                                .isEqualTo(targetObject);
    }

    private void testCreateReadDelete(String prefixUri, String inputJson, Object targetObject, Class<?> targetClass,
                                      Long targetId) {
        String createObjectUri = prefixUri;

        // create test
        createRequestWebTestClient(createObjectUri, inputJson).expectBody(targetClass)
                                                              .consumeWith(result -> {
                                                                  HttpHeaders responseHeaders =
                                                                          result.getResponseHeaders();
                                                                  URI location = responseHeaders.getLocation();
                                                                  String stringifyLocation = location.toString();
                                                                  assertThat(stringifyLocation).isEqualTo(String.format("%s/%d", createObjectUri, targetId));
                                                              });


        // read objects test
        String readObjectsUri = prefixUri;

        readRequestWebTestClient(readObjectsUri).isOk()
                                                .expectHeader()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .expectBodyList(targetClass)
                                                .hasSize(1)
                                                .consumeWith(result -> assertResponse(result, targetObject));


        // read single object test
        String readOrDeleteObjectUri = getRequestUri(prefixUri, targetId);

        readRequestWebTestClient(readOrDeleteObjectUri).isOk()
                                                       .expectHeader()
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .expectBody(targetClass)
                                                       .consumeWith(result -> assertResponse(result, targetObject));

        String incorrectReadStationUri = getRequestUri(prefixUri, -1);

        readRequestWebTestClient(incorrectReadStationUri).isNoContent()
                                                         .expectBody(Void.class);


        // delete test
        webTestClient.delete()
                     .uri(readOrDeleteObjectUri)
                     .exchange()
                     .expectStatus()
                     .isOk();

        readRequestWebTestClient(readOrDeleteObjectUri).isNoContent()
                                                       .expectBody(Void.class);

    }
}
