package atdd.station;

import atdd.station.application.dto.StationResponseDto;
import atdd.station.application.dto.SubwayCommonResponseDto;
import atdd.station.application.dto.SubwayLineResponseDto;
import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.station.web.dto.SubwayLineCreateRequestDto;
import atdd.station.web.dto.SubwaySectionCreateRequestDto;
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
        createResource("/subway-lines", SubwayLineCreateRequestDto.of("2호선"));
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    public void getSubwayLines() {
        // given
        createResource("/subway-lines", SubwayLineCreateRequestDto.of("2호선"));
        createResource("/subway-lines", SubwayLineCreateRequestDto.of("8호선"));

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
        String locationPath = createResource("/subway-lines", SubwayLineCreateRequestDto.of(subwayLineName));

        // when
        EntityExchangeResult<SubwayLineResponseDto> result = getResource(locationPath, SubwayLineResponseDto.class);

        // then
        assertThat(result.getResponseBody().getName()).isEqualTo(subwayLineName);
    }

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    public void deleteSubwayLine() {
        // given
        String locationPath = createResource("/subway-lines", SubwayLineCreateRequestDto.of("2호선"));

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

    @DisplayName("지하철 구간을 등록한다")
    @Test
    public void createSubwaySection() {
        // given
        String pathOfCreatedSubwayLine = createResource("/subway-lines", SubwayLineCreateRequestDto.of("2호선"));
        String pathOfCreatedStation = createResource("/stations", SubwayLineCreateRequestDto.of("강남역"));

        Long createdSubwayLineId = extractId(pathOfCreatedSubwayLine);
        Long createdStationId1 = extractId(pathOfCreatedStation);
        Long createdStationId2 = extractId(createResource("/stations", SubwayLineCreateRequestDto.of("역삼역")));
        Long createdStationId3 = extractId(createResource("/stations", SubwayLineCreateRequestDto.of("선릉역")));
        Long createdStationId4 = extractId(createResource("/stations", SubwayLineCreateRequestDto.of("삼성역")));
        Long createdStationId5 = extractId(createResource("/stations", SubwayLineCreateRequestDto.of("교대역")));

        // when
        createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(createdStationId1, createdStationId2));
        createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(createdStationId3, createdStationId4));
        createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(createdStationId2, createdStationId3));
        createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(createdStationId5, createdStationId1));

        // then
        EntityExchangeResult<SubwayLineResponseDto> savedSubwayLine = getResource(pathOfCreatedSubwayLine, SubwayLineResponseDto.class);
        assertThat(savedSubwayLine.getResponseBody().getStations())
                .isEqualTo(Arrays.asList(SubwayCommonResponseDto.of(Station.of("교대역"))
                        , SubwayCommonResponseDto.of(Station.of("강남역"))
                        , SubwayCommonResponseDto.of(Station.of("역삼역"))
                        , SubwayCommonResponseDto.of(Station.of("선릉역"))
                        , SubwayCommonResponseDto.of(Station.of("삼성역"))
                ));

        // and
        EntityExchangeResult<StationResponseDto> result = getResource(pathOfCreatedStation, StationResponseDto.class);
        assertThat(result.getResponseBody().getLines()).contains(SubwayCommonResponseDto.of(SubwayLine.of("2호선")));
    }

    @DisplayName("지하철 구간을 제외한다")
    @Test
    public void deleteSubwaySection() {
        // given
        String pathOfCreatedSubwayLine = createResource("/subway-lines", SubwayLineCreateRequestDto.of("2호선"));
        String pathOfCreatedStation = createResource("/stations", SubwayLineCreateRequestDto.of("역삼역"));

        Long createdSubwayLineId = extractId(pathOfCreatedSubwayLine);
        Long createdStationId2 = extractId(pathOfCreatedStation);
        Long createdStationId1 = extractId(createResource("/stations", SubwayLineCreateRequestDto.of("강남역")));
        Long createdStationId3 = extractId(createResource("/stations", SubwayLineCreateRequestDto.of("선릉역")));

        createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(createdStationId1, createdStationId2));
        createResource("/subway-lines/" + createdSubwayLineId + "/subway-section", SubwaySectionCreateRequestDto.of(createdStationId2, createdStationId3));

        // when
        webTestClient.delete()
                .uri("/subway-lines/" + createdSubwayLineId + "/subway-section?" + "stationName=역삼역")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        // then
        EntityExchangeResult<StationResponseDto> savedStation = getResource(pathOfCreatedStation, StationResponseDto.class);
        assertThat(savedStation.getResponseBody().getLines()).isEmpty();

        // and
        EntityExchangeResult<SubwayLineResponseDto> savedSubwayLine = getResource(pathOfCreatedSubwayLine, SubwayLineResponseDto.class);
        assertThat(savedSubwayLine.getResponseBody().getStations())
                .isEqualTo(Arrays.asList(
                        SubwayCommonResponseDto.of(Station.of("강남역")), SubwayCommonResponseDto.of(Station.of("선릉역"))));
    }

}
