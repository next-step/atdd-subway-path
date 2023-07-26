package nextstep.subway.unit.line;

import nextstep.subway.line.domain.ShortestPathFinder;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.entity.addition.SectionAdditionHandlerMapping;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("최단 경로 찾기 알고리즘")
public class ShortestPathFinderTest {

    Station 교대역 = new Station(1L, "교대역");
    Station 남부터미널역 = new Station(3L, "남부터미널역");
    Station 양재역 = new Station(4L, "양재역");
    Station 강남역 = new Station(2L, "강남역");

    Line 이호선;
    Line 삼호선;
    Line 신분당선;

    /**
     * 교대역 --- *2호선* --- 강남역
     * ㅣ                     ㅣ
     * *3호선*              *신분당선*
     * ㅣ                       ㅣ
     * 남부터미널역 --- *3호선* --- 양재역
     */

    @BeforeEach
    void setUp() {
        이호선 = new Line("이호선", "green", 10, 교대역, 강남역);
        삼호선 = new Line("삼호선", "orange", 2, 교대역, 남부터미널역);
        신분당선 = new Line("신분당선", "red", 3, 강남역, 양재역);
        삼호선.addSection(new SectionAdditionHandlerMapping(), new Section(삼호선, 남부터미널역, 양재역, 3));
    }

    @DisplayName("최단 경로 길 찾기")
    @Test
    void findShortestPath() {
        // given
        List<Line> lineList = List.of(이호선, 삼호선, 신분당선);

        // when
        ShortestPathFinder pathFinder = new ShortestPathFinder(lineList, 교대역, 강남역);
        List<Station> searchedPath = pathFinder.getPath();
        BigInteger weight = pathFinder.getWeight();

        // then
        assertThat(searchedPath.stream()
                .map(Station::getId)
                .collect(Collectors.toList()))
                .containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId(), 강남역.getId());
        assertThat(weight).isEqualTo(8);
    }

    @DisplayName("최단 경로 길 찾기, 반대")
    @Test
    void findShortestPathReverse() {
        // given
        List<Line> lineList = List.of(이호선, 삼호선, 신분당선);

        // when
        ShortestPathFinder pathFinder = new ShortestPathFinder(lineList, 강남역, 교대역);
        List<Station> searchedPath = pathFinder.getPath();
        BigInteger weight = pathFinder.getWeight();

        // then
        assertThat(searchedPath.stream()
                .map(Station::getId)
                .collect(Collectors.toList()))
                .containsExactly(강남역.getId(), 양재역.getId(), 남부터미널역.getId(), 교대역.getId());
        assertThat(weight).isEqualTo(8);
    }
}
