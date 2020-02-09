package atdd.station;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    /*
    Feature: 지하철역 관리

Scenario: 지하철역 등록
When 관리자는 "강남역" 지하철역 등록을 요청한다.
Then "강남역" 지하철역이 등록된다.

Scenario: 지하철역 목록 조회
Given "강남역" 지하철역이 등록되어 있다.
When 사용자는 지하철역 목록조회를 요청한다.
Then 사용자는 "강남역" 지하철역의 정보를 응답받는다.

Scenario: 지하철역 정보 조회
Given "강남역" 지하철역이 등록되어 있다.
When 사용자는 "강남역" 지하철역의 정보 조회를 요청한다.
Then 사용자는 "강남역" 지하철역의 정보를 응답받는다.

Scenario: 지하철역 삭제
Given "강남역" 지하철역이 등록되어 있다.
When 관리자는 "강남역" 지하철역 삭제를 요청한다.
Then "강남역" 지하철역이 삭제되었다.

     */
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void test() {
        String stationName = "강남역";
        String inputJson = "{\"name\":\""+stationName+"\"}";


        webTestClient.post().uri("/stations")
                .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(stationName);

        /*

        Scenario: 지하철역 목록 조회
        Given "강남역" 지하철역이 등록되어 있다.
        When 사용자는 지하철역 목록조회를 요청한다.
        Then 사용자는 "강남역" 지하철역의 정보를 응답받는다.
         */
        webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(stationName);

    }
}
