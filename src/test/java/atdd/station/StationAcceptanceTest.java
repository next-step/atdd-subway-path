package atdd.station;

import atdd.station.web.dto.StationRequestDto;
import atdd.station.web.dto.StationResponseDto;

import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void test() {
        String stationName = "강남역";
        String inputJson = "{\"name\":\""+stationName+"\"}";

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(stationName);
    }

    @DisplayName("지하철역 등록 테스트")
    @Test
    public void createStationTest() {
        //given
        String stationName1 = "강남역";
        String stationName2 = "수서역";
        StationRequestDto stationRequestDto = StationRequestDto.builder().name(stationName1).build();
        StationRequestDto.builder().name(stationName2).build();

        //when
        StationResponseDto stationResponseDto = webTestClient.post().uri("/stations/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(stationRequestDto),StationRequestDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(StationResponseDto.class)
                .returnResult()
                .getResponseBody();
        //then
        assertThat(stationResponseDto.getName()).isEqualTo(stationRequestDto.getName());

    }

    @DisplayName("지하철역 목록 조회")
    @Test
    public void selectStationList() {
        //given
        createStationTest();

        //when
        List<StationResponseDto> stationResponseDtoList = webTestClient.get().uri("/stations/list")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(StationResponseDto.class)
                .returnResult()
                .getResponseBody();

        //then
        stationResponseDtoList.forEach(System.out::println);
    }

    @DisplayName("지하철역 정보 조회")
    @Test
    public void selectStation() {
        //given
        Long id = 1L;
        //when
        StationResponseDto stationResponseDto = webTestClient.get().uri("/stations/list/"+id)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StationResponseDto.class)
                .returnResult()
                .getResponseBody();
        //then
        assertThat(stationResponseDto.getName()).isEqualTo("강남역");
        assertThat(stationResponseDto.getId()).isEqualTo(1L);
    }

    @DisplayName("지하철역 정보 삭제")
    @Test
    public void deleteStation() {
        //given
        Long id = 1L;
        //when
        webTestClient.post().uri("stations/delete/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

}
