package atdd.line;

import atdd.AcceptanceTestSupport;
import atdd.line.controller.LineController;
import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AcceptanceTestSupport {

    private LineHttpTestSupport lineHttpTestSupport;

    @BeforeEach
    void setup() {
        lineHttpTestSupport = new LineHttpTestSupport(webTestClient);
    }

    @DisplayName("지하철 노선 등록")
    @Test
    public void create() {
        final String name = "2호선";
        final LocalTime startTime = LocalTime.of(5, 0);
        final LocalTime endTime = LocalTime.of(23, 50);
        final int interval = 10;

        LineCreateRequestDto requestDto = new LineCreateRequestDto(name, startTime, endTime, interval);


        final EntityExchangeResult<LineResponseDto> result = webTestClient.post()
                .uri(LineController.ROOT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), LineCreateRequestDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(LineResponseDto.class)
                .returnResult();


        final LineResponseDto responseDto = result.getResponseBody();
        final String location = result.getResponseHeaders().getLocation().getPath();
        assertThat(location).isEqualTo(LineController.ROOT_URI + "/" + responseDto.getId());
        assertThat(responseDto.getName()).isEqualTo(name);
        assertThat(responseDto.getStartTime()).isEqualTo(startTime);
        assertThat(responseDto.getEndTime()).isEqualTo(endTime);
        assertThat(responseDto.getIntervalTime()).isEqualTo(interval);
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void findAll() {
        final LocalTime startTime = LocalTime.of(5, 0);
        final LocalTime endTime = LocalTime.of(23, 50);
        final int interval = 10;

        final String name1 = "2호선";
        final String name2 = "3호선";

        final LineCreateRequestDto requestDto1 = new LineCreateRequestDto(name1, startTime, endTime, interval);
        final LineCreateRequestDto requestDto2 = new LineCreateRequestDto(name2, startTime, endTime, interval);
        lineHttpTestSupport.createLine(requestDto1);
        lineHttpTestSupport.createLine(requestDto2);

        final EntityExchangeResult<List<LineResponseDto>> result = webTestClient.get().uri(
                LineController.ROOT_URI)
                .exchange()
                .expectBodyList(LineResponseDto.class)
                .returnResult();


        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        final List<LineResponseDto> lineResponseDtos = result.getResponseBody();
        assertThat(lineResponseDtos).hasSize(2);
        assertEqual(lineResponseDtos.get(0), requestDto1);
        assertEqual(lineResponseDtos.get(1), requestDto2);
    }

    @DisplayName("지하철 노선 정보 조회")
    @Test
    void findAllByName() {
        final LocalTime startTime = LocalTime.of(5, 0);
        final LocalTime endTime = LocalTime.of(23, 50);
        final int interval = 10;

        final String name1 = "2호선";
        final String name2 = "3호선";

        final LineCreateRequestDto requestDto1 = new LineCreateRequestDto(name1, startTime, endTime, interval);
        final LineCreateRequestDto requestDto2 = new LineCreateRequestDto(name2, startTime, endTime, interval);
        lineHttpTestSupport.createLine(requestDto1);
        lineHttpTestSupport.createLine(requestDto2);


        final String requestURI = UriComponentsBuilder.fromUriString(LineController.ROOT_URI)
                .queryParam("name", name1)
                .build()
                .toUriString();

        final EntityExchangeResult<List<LineResponseDto>> result = webTestClient.get()
                .uri(requestURI)
                .acceptCharset(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBodyList(LineResponseDto.class)
                .returnResult();


        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        final List<LineResponseDto> lineResponseDtos = result.getResponseBody();
        assertThat(lineResponseDtos).hasSize(1);
        assertThat(lineResponseDtos.get(0).getName()).isEqualTo(name1);
    }

    private void assertEqual(LineResponseDto responseDto, LineCreateRequestDto requestDto) {
        assertThat(responseDto.getName()).isEqualTo(requestDto.getName());
        assertThat(responseDto.getStartTime()).isEqualTo(requestDto.getStartTime());
        assertThat(responseDto.getEndTime()).isEqualTo(requestDto.getEndTime());
        assertThat(responseDto.getIntervalTime()).isEqualTo(requestDto.getIntervalTime());
    }

}
