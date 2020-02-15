package atdd.line;

import atdd.AcceptanceTestSupport;
import atdd.line.controller.LineController;
import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import reactor.core.publisher.Mono;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AcceptanceTestSupport {

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

}
