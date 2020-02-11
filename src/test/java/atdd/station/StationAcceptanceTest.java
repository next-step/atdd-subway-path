package atdd.station;

import atdd.station.domain.station.Station;
import atdd.station.web.dto.StationListResponseDto;
import atdd.station.web.dto.StationRequestDto;
import atdd.station.web.dto.StationResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void createStationTest() {
        //given
        String name = "강남역";
        StationRequestDto stationRequestDto = StationRequestDto.builder().name(name).build();

        //when
        StationResponseDto stationResponseDto = webTestClient.post().uri("createStation")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(stationRequestDto),StationRequestDto.class)
                .exchange()
                .expectStatus().isCreated()//받고싶은 uri경로 생성확인
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")//받고싶은 uri경로값을 저장하고있는객체
                .expectBody(StationResponseDto.class)
                .returnResult()
                .getResponseBody();

        //then
        assertThat(stationResponseDto.getName()).isEqualTo(name);

    }

    @Test
    public void selectStationListTest() {

        webTestClient.get().uri("selectStationList")
                .accept(MediaType.APPLICATION_JSON) //응답으로 받고싶은데이터유형
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)//응답 데이터유형 확인
                .expectBody()
                .jsonPath("$.[0].name").isEqualTo("강남역")
                .jsonPath("$.[1].name").isEqualTo("수서역");
    }

}
