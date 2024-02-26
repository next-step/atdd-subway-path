package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Path;

public class PathTest {
    @Test
    void testGetStationNamesAlongPath_경로_사이의_모든_역과_최단거리를_반환한다() {
        //given
        String 교대역 = "교대역";
        String 남부터미널역 = "남부터미널역";
        String 양재역 = "양재역";
        int 교대역_남부터미널역_사이_거리 = 10;
        int 남부터미널역_양재역_사이_거리 = 12;
        Path path = new Path();
        path.addConnectionBetweenStations(교대역, 남부터미널역, 교대역_남부터미널역_사이_거리);
        path.addConnectionBetweenStations(남부터미널역, 양재역, 남부터미널역_양재역_사이_거리);

        //when
        List<String> stationNamesAlongPath = path.getStationNamesAlongPath(교대역, 양재역);
        int shortestDistanceBetweenStations = path.getShortestDistanceBetweenStations(교대역, 양재역);
        //then
        assertAll(
            () -> assertThat(stationNamesAlongPath).hasSize(3),
            () -> assertThat(stationNamesAlongPath).contains(교대역, 남부터미널역, 양재역),
            () -> assertThat(shortestDistanceBetweenStations).isEqualTo(교대역_남부터미널역_사이_거리 + 남부터미널역_양재역_사이_거리)
        );
    }

}
