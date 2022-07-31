package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathTest {
    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;
    Station 상도역;
    Station 이수역;

    Line 이호선;
    Line 삼호선;
    Line 신분당선;
    Line 칠호선;

    List<Section> sectionList;

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");
        상도역 = new Station(5L, "상도역");
        이수역 = new Station(6L, "이수역");

        이호선 = new Line("2호선", "green");
        삼호선 = new Line("3호선", "orange");
        신분당선 = new Line("신분당선", "red");
        칠호선 = new Line("7호선", "dark green");

        이호선.addSection(교대역, 강남역, 10);
        삼호선.addSection(교대역, 남부터미널역, 10);
        신분당선.addSection(교대역, 남부터미널역, 10);
        삼호선.addSection(남부터미널역, 양재역, 3);
        칠호선.addSection(상도역, 이수역, 5);

        List<Line> lineList = Arrays.asList(이호선, 삼호선, 신분당선, 칠호선);
        sectionList = lineList.stream()
                .flatMap(line -> line.getSectionList()
                        .stream())
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("최단경로 조회")
    void getShortestPaths() {
        //when
        Path path = new Path(sectionList);
        List<Station> stationList = path.getShortestPath(교대역, 양재역);
        //then
        assertThat(stationList).hasSize(3)
                .containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.getShortestDistance(교대역, 양재역))
                .isEqualTo(10 + 3);
    }

    @DisplayName("최단경로 조회시, sourceStation과 targetStation을 연결해주는 경로가 없다")
    @Test
    void getShortestPaths_fail_source_target_not_connected() {
        //when & then
        Path path = new Path(sectionList);
        assertThatThrownBy(() -> path.getShortestPath(교대역, 상도역)).isInstanceOf(BusinessException.class);
    }

}