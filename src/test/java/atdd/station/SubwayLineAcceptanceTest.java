package atdd.station;

import atdd.station.application.dto.StationResponseDto;
import atdd.station.application.dto.SubwayCommonResponseDto;
import atdd.station.application.dto.SubwayLineResponseDto;
import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.support.SubwayAcceptanceTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class SubwayLineAcceptanceTest extends SubwayAcceptanceTestSupport {
    @DisplayName("지하철 노선을 등록한다")
    @Test
    public void createSubwayLine() {
        // expect
        createSubwayLineResource("2호선");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    public void getSubwayLines() {
        // given
        createSubwayLineResource("2호선");
        createSubwayLineResource("8호선");

        // when, then
        webTestClient.get()
                .uri("/subway-lines")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(SubwayLineResponseDto.class)
                .hasSize(2)
                .isEqualTo(Arrays.asList(SubwayLineResponseDto.of(SubwayLine.of("2호선"), Collections.emptyList())
                        , SubwayLineResponseDto.of(SubwayLine.of("8호선"), Collections.emptyList())));
    }

    @DisplayName("지하철 노선 정보를 조회한다.")
    @Test
    public void retrieveFromSubwayLineName() {
        // given
        String subwayLineName = "2호선";
        Long createdSubwayLineId = createSubwayLineResource("2호선");

        // when
        EntityExchangeResult<SubwayLineResponseDto> result = getSubwayLineResource(createdSubwayLineId);

        // then
        assertThat(result.getResponseBody().getName()).isEqualTo(subwayLineName);
    }

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    public void deleteSubwayLine() {
        // given
        Long createdSubwayLineId = createSubwayLineResource("2호선");

        // when
        deleteSubwayLineResource(createdSubwayLineId);

        // then
        webTestClient.get()
                .uri("/subway-lines/" + createdSubwayLineId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @DisplayName("지하철 구간을 등록한다")
    @Test
    public void createSubwaySection() {
        // given
        Long createdSubwayLineId = createSubwayLineResource("2호선");

        Long createdStationId1 = createStationResource("강남역");
        Long createdStationId2 = createStationResource("역삼역");
        Long createdStationId3 = createStationResource("선릉역");
        Long createdStationId4 = createStationResource("삼성역");
        Long createdStationId5 = createStationResource("교대역");

        // when
        createSubwaySectionResource(createdSubwayLineId, createdStationId1, createdStationId2);
        createSubwaySectionResource(createdSubwayLineId, createdStationId3, createdStationId4);
        createSubwaySectionResource(createdSubwayLineId, createdStationId2, createdStationId3);
        createSubwaySectionResource(createdSubwayLineId, createdStationId5, createdStationId1);

        // then
        EntityExchangeResult<SubwayLineResponseDto> savedSubwayLine = getSubwayLineResource(createdSubwayLineId);
        assertThat(savedSubwayLine.getResponseBody().getStations())
                .containsExactly(SubwayCommonResponseDto.of(Station.of("교대역"))
                        , SubwayCommonResponseDto.of(Station.of("강남역"))
                        , SubwayCommonResponseDto.of(Station.of("역삼역"))
                        , SubwayCommonResponseDto.of(Station.of("선릉역"))
                        , SubwayCommonResponseDto.of(Station.of("삼성역"))
                );

        // and
        EntityExchangeResult<StationResponseDto> result = getStationResource(createdStationId1);
        assertThat(result.getResponseBody().getLines()).contains(SubwayCommonResponseDto.of(SubwayLine.of("2호선")));
    }

    @DisplayName("지하철 구간을 제외한다")
    @Test
    public void deleteSubwaySection() {
        // given
        Long createdSubwayLineId = createSubwayLineResource("2호선");

        Long createdStationId1 = createStationResource("강남역");
        Long createdStationId2 = createStationResource("역삼역");
        Long createdStationId3 = createStationResource("선릉역");

        createSubwaySectionResource(createdSubwayLineId, createdStationId1, createdStationId2);
        createSubwaySectionResource(createdSubwayLineId, createdStationId2, createdStationId3);

        // when
        deleteSubwaySectionResource(createdSubwayLineId, "역삼역");

        // then
        EntityExchangeResult<StationResponseDto> savedStation = getStationResource(createdStationId2);
        assertThat(savedStation.getResponseBody().getLines()).isEmpty();

        // and
        EntityExchangeResult<SubwayLineResponseDto> savedSubwayLine = getSubwayLineResource(createdSubwayLineId);
        assertThat(savedSubwayLine.getResponseBody().getStations())
                .isEqualTo(Arrays.asList(
                        SubwayCommonResponseDto.of(Station.of("강남역")), SubwayCommonResponseDto.of(Station.of("선릉역"))));
    }

}
