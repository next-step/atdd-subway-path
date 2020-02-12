package atdd.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName(value = "Station Controller 를 테스트한다")
public class StationAcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Nested
    @DisplayName(value = "역을 생성하는 API")
    class CreateStation {

        @Nested
        @DisplayName(value = "역 이름이 주어진다면")
        class GivenStationName {

            @ParameterizedTest
            @ValueSource(strings = {"강남역", "역삼역", "공덕역"})
            @DisplayName(value = "생성된 역을 리턴한다")
            void expectCreateStation(String stationName) {
                createStationBy(stationName);
            }
        }
    }

    @Nested
    @DisplayName(value = "역을 찾아오는 API")
    class FindStation {

        @Nested
        @DisplayName(value = "패스가 주어진다면")
        class GivenStation {

            final String stationName = "강남역";
            final String path = createStationBy(stationName).getResponseHeaders().getLocation().getPath();

            @Test
            @DisplayName("조회가 제대로 되는지 확인한다")
            @Order(2)
            void expectGetStation() {
                assertThat(webTestClient.get().uri(path)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(StationDto.Response.class)
                        .returnResult()
                        .getResponseBody()
                        .getName())
                        .isEqualTo(stationName);
            }
        }

        @Nested
        @DisplayName(value = "역이 세 개가 주어진다면")
        class GivenAll {

            @BeforeEach
            void setUp() {
                createStationBy("강남역");
                createStationBy("역삼역");
            }

            @Test
            @DisplayName("조회가 제대로 되는지 확인한다")
            void expectGetStations() {

                webTestClient.get().uri(StationUri.ROOT)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBodyList(StationDto.Response.class)
                        .hasSize(2)
                        .isEqualTo(Arrays.asList(
                                StationDto.Response.from(Station.of("강남역")),
                                StationDto.Response.from(Station.of("역삼역")))
                        );
            }
        }

    }

    @Nested
    @DisplayName(value = "역을 삭제하는 API")
    class DeleteStation {

        @Nested
        @DisplayName(value = "패스가 주어진다면")
        class GivenStation {

            final String stationName = "강남역";
            final String path = createStationBy(stationName).getResponseHeaders().getLocation().getPath();

            @Test
            @DisplayName("삭제가 잘 되는지 확인한다")
            void expectDeleteStation() {
                //when
                webTestClient.delete().uri(path)
                        .exchange()
                        .expectStatus().isOk();

                //then
                webTestClient.delete().uri(path)
                        .exchange()
                        .expectStatus().isNotFound();
            }
        }
    }


    public EntityExchangeResult<Station> createStationBy(String name) {

        final String inputJson = "{\"name\":\"" + name + "\"}";

        return webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Station.class)
                .returnResult();
    }
}
