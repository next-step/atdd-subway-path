package nextstep.subway.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.PathFinder;
import nextstep.subway.path.PathResponse;
import nextstep.subway.station.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PathFinderTest {

    PathFinder pathFinder = new PathFinder();

    @Test
    void getPath() {
        //given
        Station 교대역 = new Station("교대역");
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 남부터미널역 = new Station("남부터미널역");

        Line 이호선 = new Line("2호선", "green");
        이호선.generateSection(10, 교대역, 강남역);

        Line 신분당선 = new Line("신분당선", "red");
        신분당선.generateSection(10, 강남역, 양재역);

        Line 삼호선 = new Line("3호선", "orange");
        삼호선.generateSection(2, 교대역, 남부터미널역);
        삼호선.generateSection(3, 남부터미널역, 양재역);

        //when
        PathResponse result = pathFinder.getPath(삼호선.getSections(), 교대역, 양재역);

        //then
        assertThat(result.getStations()).containsExactly(
                StationResponse.ofEntity(교대역),
                StationResponse.ofEntity(남부터미널역),
                StationResponse.ofEntity(양재역)
        );

        assertThat(result.getDistance()).isEqualTo(5);
    }
}