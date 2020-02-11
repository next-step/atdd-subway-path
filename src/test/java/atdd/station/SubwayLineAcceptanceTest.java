package atdd.station;

import atdd.station.application.dto.SubwayLineResponseDto;
import atdd.station.domain.SubwayLine;
import atdd.support.SubwayAcceptanceTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SubwayLineAcceptanceTest extends SubwayAcceptanceTestSupport {

    @DisplayName("지하철 노선을 등록한다")
    @Test
    public void create() {
        // expect
        createSubwayLine("2호선");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    public void getSubwayLines() {
        // given
        createSubwayLine("2호선");
        createSubwayLine("8호선");

        // when, then
        webTestClient.get()
                .uri("/subway-lines")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(SubwayLineResponseDto.class)
                .hasSize(2)
                .isEqualTo(Arrays.asList(SubwayLineResponseDto.of(SubwayLine.of("2호선")), SubwayLineResponseDto.of(SubwayLine.of("8호선"))));
    }

    @DisplayName("지하철 노선 정보를 조회한다.")
    @Test
    public void retrieveFromSubwayLineName() {
        // given
        String subwayLineName = "2호선";
        EntityExchangeResult<Void> createdResult = createSubwayLine(subwayLineName);
        String locationPath = createdResult.getResponseHeaders().getLocation().getPath();

        // when
        EntityExchangeResult<SubwayLineResponseDto> result = getSubwayLineFromLocationPath(locationPath);

        // then
        assertThat(result.getResponseBody().getName()).isEqualTo(subwayLineName);
    }


    @DisplayName("지하철 노선을 삭제한다")
    @Test
    public void deleteFromSubwayLineName() {
        // given
        EntityExchangeResult<Void> createdResult = createSubwayLine("2호선");
        String locationPath = createdResult.getResponseHeaders().getLocation().getPath();

        // when
        webTestClient.delete()
                .uri(locationPath)
                .exchange()
                .expectStatus().isOk();

        // then
        webTestClient.get()
                .uri(locationPath)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

}
