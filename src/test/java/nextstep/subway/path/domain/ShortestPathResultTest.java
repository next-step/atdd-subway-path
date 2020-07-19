package nextstep.subway.path.domain;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("최단 경로 조회 결과 테스트")
class ShortestPathResultTest {

    @DisplayName("result 결과에서 LineStationResponse를 추출")
    @Test
    public void toLineStationResponseTest() {
        // given
        List<StationResponse> stationResponses = new ArrayList<>();
        StationResponse station1 = new StationResponse(1L, "역1", LocalDateTime.now(), LocalDateTime.now());
        StationResponse station2 = new StationResponse(2L, "역2", LocalDateTime.now(), LocalDateTime.now());
        StationResponse station3 = new StationResponse(3L, "역3", LocalDateTime.now(), LocalDateTime.now());
        stationResponses.add(station1);
        stationResponses.add(station2);
        stationResponses.add(station3);

        List<LineStationResponse> lineStationResponses = new ArrayList<>();
        LineStationResponse lineStation1 = new LineStationResponse(station1, null, 10, 10);
        LineStationResponse lineStation2 = new LineStationResponse(station2, station1.getId(), 10, 10);
        LineStationResponse lineStation3 = new LineStationResponse(station3, station2.getId(), 10, 10);
        lineStationResponses.add(lineStation1);
        lineStationResponses.add(lineStation2);
        lineStationResponses.add(lineStation3);

        LineResponse line = new LineResponse(1L, "부산1호선", "BLUE", LocalTime.now(), LocalTime.now(), 10, lineStationResponses, LocalDateTime.now(), LocalDateTime.now());

        ShortestPathResult result = ShortestPathResult.withResult(stationResponses, 10.0);

        // when
        List<LineStationResponse> extractedLineStationResponses = result.toLineStationResponse(Arrays.asList(line));

        // then
        assertThat(extractedLineStationResponses).containsExactly(lineStation1, lineStation2, lineStation3);

    }

}