package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Path;
import nextstep.subway.exception.IllegalPathException;

public class PathTest {
    private static final String 교대역 = "교대역";
    private static final String 남부터미널역 = "남부터미널역";
    private static final String 양재역 = "양재역";
    private static final int 교대역_남부터미널역_사이_거리 = 10;
    private static final int 남부터미널역_양재역_사이_거리 = 12;
    @Test
    void testGetStationNamesAlongPath_경로_사이의_모든_역과_최단거리를_반환한다() {
        //given
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

    @Test
    void testValidatePath_출발역과_도착역이_같으면_예외를_반환한다() {
        //given
        Path path = new Path();
        path.addConnectionBetweenStations(교대역, 남부터미널역, 교대역_남부터미널역_사이_거리);
        path.addConnectionBetweenStations(남부터미널역, 양재역, 남부터미널역_양재역_사이_거리);

        //when & then
        assertThatThrownBy(() -> path.getStationNamesAlongPath(교대역, 교대역)).isInstanceOf(IllegalPathException.class);
    }
}
