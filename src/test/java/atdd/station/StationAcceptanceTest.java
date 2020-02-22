package atdd.station;

import atdd.line.Line;
import atdd.station.support.EdgeHttpSupport;
import atdd.station.support.LineHttpSupport;
import atdd.station.support.StationHttpSupport;
import org.assertj.core.api.HamcrestCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.web.server.Http2;
import org.springframework.http.HttpMessage;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import javax.print.DocFlavor;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName(value = "Station Controller 를 테스트한다")
public class StationAcceptanceTest extends AbstractWebTestClientTest {

    @DisplayName(value = "역을 생성하는 API")
    @Nested
    class CreateStation {

        @DisplayName(value = "역 이름이 주어진다면")
        @Nested
        class GivenStationName {


            @DisplayName(value = "생성된 역을 리턴한다")
            @ParameterizedTest
            @ValueSource(strings = {"강남역", "역삼역", "공덕역"})
            void expectCreateStation(String stationName) {

                StationHttpSupport.create(webTestClient, stationName);
            }
        }
    }

    @DisplayName(value = "역을 찾아오는 API")
    @Nested
    class FindStation {

        @DisplayName(value = "패스가 주어진다면")
        @Nested
        class GivenStation {

            final String stationName1 = "강남역";

            final String stationName2 = "역삼역";

            final String stationName3 = "회현역";

            String path = "";

            @BeforeEach
            void setUp() {
                EntityExchangeResult<Station> station = StationHttpSupport.create(webTestClient, stationName1);
                path = station.getResponseHeaders().getLocation().getPath();

                Station station1 = station.getResponseBody();
                Station station2 = StationHttpSupport.create(webTestClient, stationName2).getResponseBody();
                Station station3 = StationHttpSupport.create(webTestClient, stationName3).getResponseBody();

                Line line = LineHttpSupport.create(webTestClient, "2호선").getResponseBody();
                Line line2 = LineHttpSupport.create(webTestClient, "분당선").getResponseBody();


                EdgeHttpSupport.createEdge(webTestClient, line.getId(), station1.getId(), station2.getId());
                EdgeHttpSupport.createEdge(webTestClient, line2.getId(), station1.getId(), station3.getId());

            }

            @DisplayName("조회가 제대로 되는지 확인한다")
            @Test
            void expectGetStation() {
                EntityExchangeResult<StationDto.Response> result = webTestClient.get().uri(path)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody(StationDto.Response.class)
                        .returnResult();

                Set<String> names = result.getResponseBody()
                        .getLines()
                        .stream()
                        .map(StationDto.Response.LineDto::getName)
                        .collect(Collectors.toSet());

                assertThat(names).contains("2호선");
                assertThat(names).contains("분당선");

            }
        }

        @DisplayName(value = "역이 두 개가 주어진다면")
        @Nested
        class GivenAll {

            @BeforeEach
            void setUp() {
                StationHttpSupport.create(webTestClient, "강남역");
                StationHttpSupport.create(webTestClient, "역삼역");
            }

            @DisplayName("조회가 제대로 되는지 확인한다")
            @Test
            void expectGetStations() {
                webTestClient.get().uri(StationUri.ROOT)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBodyList(StationDto.Response.class)
                        .value(stations -> stations.stream().anyMatch(station -> station.getName().equals("강남역")));
            }
        }

    }

    @DisplayName(value = "역을 삭제하는 API")
    @Nested
    class DeleteStation {

        @DisplayName(value = "패스가 주어진다면")
        @Nested
        class GivenStation {

            final String stationName = "강남역";
            final String path = StationHttpSupport.create(webTestClient, stationName).getResponseHeaders().getLocation().getPath();

            @DisplayName("삭제가 잘 되는지 확인한다")
            @Test
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
}
