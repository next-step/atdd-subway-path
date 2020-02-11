package atdd.station;

import atdd.station.application.dto.StationResponseDto;
import atdd.station.application.dto.SubwayCommonResponseDto;
import atdd.station.application.dto.SubwayLineResponseDto;
import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.support.SubwayAcceptanceTestSupport;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SubwaySectionAcceptanceTest extends SubwayAcceptanceTestSupport {
    private Long createdStationId1;
    private Long createdStationId2;
    private Long createdStationId3;
    private Long createdSubwayLineId;

    @BeforeEach
    void setUp() {
        createdStationId1 = extractId(createStation("강남역"));
        createdStationId2 = extractId(createStation("역삼역"));
        createdStationId3 = extractId(createStation("선릉역"));

        createdSubwayLineId = extractId(createSubwayLine("2호선"));
    }

    @DisplayName("지하철 구간을 등록한다")
    @Test
    public void create() throws JSONException {
        // when
        createSubwaySection(createdSubwayLineId, createdStationId1, createdStationId2);

        // then
        EntityExchangeResult<SubwayLineResponseDto> savedSubwayLine = getSubwayLineFromId(createdSubwayLineId);
        assertThat(savedSubwayLine.getResponseBody().getStations())
                .isEqualTo(Arrays.asList(SubwayCommonResponseDto.of(Station.of("강남역")),
                        SubwayCommonResponseDto.of(Station.of("역삼역"))));

        // and
        EntityExchangeResult<StationResponseDto> result = getStationFromId(createdStationId1);
        assertThat(result.getResponseBody().getLines()).contains(SubwayCommonResponseDto.of(SubwayLine.of("2호선")));
    }

    @DisplayName("지하철 구간을 제외한다")
    @Test
    public void remove() {
        // given
        createSubwaySection(createdSubwayLineId, createdStationId1, createdStationId2);
        createSubwaySection(createdSubwayLineId, createdStationId2, createdStationId3);

        // when
        webTestClient.delete()
                .uri("subway-sections?subwayLineId=" + createdSubwayLineId + "&stationName=역삼역")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        // then
        EntityExchangeResult<StationResponseDto> savedStation = getStationFromId(createdStationId2);
        assertThat(savedStation.getResponseBody().getLines()).isEmpty();

        // and
        EntityExchangeResult<SubwayLineResponseDto> savedSubwayLine = getSubwayLineFromId(createdSubwayLineId);
        assertThat(savedSubwayLine.getResponseBody().getStations())
                .isEqualTo(Arrays.asList(
                        SubwayCommonResponseDto.of(Station.of("강남역")), SubwayCommonResponseDto.of(Station.of("선릉역"))));
    }

}
