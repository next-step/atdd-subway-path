package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PathService 단위 테스트")
public class PathFinderTest {

    private PathFinder pathFinder = new PathFinder();


    private Station 강남역;
    private Station 양재역;
    private Station 교대역;
    private Station 남부터미널역;

    private Section 강남_양재;
    private Section 교대_강남;
    private Section 교대_남부;
    private Section 남부_양재;

    @BeforeEach
    void setup() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        교대역 = new Station("교대역");
        남부터미널역 = new Station("남부터미널역");

        강남_양재 = new Section(null, 강남역, 양재역, 10);
        교대_강남 = new Section(null, 교대역, 강남역, 10);
        교대_남부 = new Section(null, 교대역, 남부터미널역, 3);
        남부_양재 = new Section(null, 남부터미널역, 양재역, 2);
    }

    @DisplayName("최단거리 조회")
    @Test
    void getShortedPath() {
        StationGraphPath path = pathFinder.getShortedPath(
            Arrays.asList(강남_양재, 교대_강남, 교대_남부, 남부_양재),
            강남역,
            남부터미널역
        );

        assertThat(path.getVertexStations())
            .isEqualTo(Arrays.asList(강남역, 양재역, 남부터미널역));

        assertThat(path.getDistance())
            .isEqualTo(12);
    }
}
