package atdd.station;

import atdd.station.application.dto.ShortestPathResponseDto;
import atdd.station.application.dto.StationResponseDto;
import atdd.station.application.dto.SubwayCommonResponseDto;
import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.station.web.dto.SubwaySectionCreateRequestDto;
import atdd.support.SubwayAcceptanceTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SubwayMapAcceptanceTest extends SubwayAcceptanceTestSupport {

    @DisplayName("지하철역 사이의 최단 거리 경로를 조회한다.")
    @Test
    void getShortestPath() {
        // given
        Long createdStationId1 = extractId(createResource("/stations", "강남역"));
        Long createdStationId2 = extractId(createResource("/stations", "역삼역"));
        Long createdStationId3 = extractId(createResource("/stations", "선릉역"));
        Long createdStationId4 = extractId(createResource("/stations", "삼성역"));
        Long createdStationId5 = extractId(createResource("/stations", "교대역"));

        Long createdSubwayLineId = extractId(createResource("/subway-lines", "2호선"));

        createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(createdStationId1, createdStationId2));
        createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(createdStationId3, createdStationId4));
        createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(createdStationId2, createdStationId3));
        createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(createdStationId5, createdStationId1));

        // when
        EntityExchangeResult<ShortestPathResponseDto> result = webTestClient.get()
                .uri("/subway-map/shortest-path?startStationId=" + createdStationId5 + "&destinationStationId=" + createdStationId2)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShortestPathResponseDto.class)
                .returnResult();

        // then
        assertThat(result.getResponseBody().getStations().size()).isEqualTo(3);
        assertThat(result.getResponseBody().getStations()).isEqualTo(Arrays.asList(
                StationResponseDto.of(Station.of("교대역"), Arrays.asList(SubwayCommonResponseDto.of(SubwayLine.of("2호선")))),
                StationResponseDto.of(Station.of("강남역"), Arrays.asList(SubwayCommonResponseDto.of(SubwayLine.of("2호선")))),
                StationResponseDto.of(Station.of("역삼역"), Arrays.asList(SubwayCommonResponseDto.of(SubwayLine.of("2호선"))))
        ));
    }
}
