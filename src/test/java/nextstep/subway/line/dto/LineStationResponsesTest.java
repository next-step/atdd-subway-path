package nextstep.subway.line.dto;

import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LineStationResponsesTest {

    private LineStationResponses lineStationResponses;

    @BeforeEach
    void setUp() {
        StationResponse stationResponse1 = new StationResponse(1L, "서울역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse stationResponse2 = new StationResponse(2L, "시청역", LocalDateTime.now(), LocalDateTime.now());

        LineStationResponse lineStationResponse1 = new LineStationResponse(stationResponse1, null, 2, 2);
        LineStationResponse lineStationResponse2 = new LineStationResponse(stationResponse2, null, 2, 3);

        lineStationResponses = new LineStationResponses(Lists.newArrayList(lineStationResponse1, lineStationResponse2));
    }

    @Test
    void distance() {
        assertThat(lineStationResponses.getDistances()).isEqualTo(4);
    }

    @Test
    void duration() {
        assertThat(lineStationResponses.getDurations()).isEqualTo(5);
    }
}
