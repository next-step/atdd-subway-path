package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SubwayMap;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.FindPathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SubwayMapTest {

    Line 일호선;
    Line 이호선;
    Line 삼호선;
    Line 신분당선;

    Section 용산_서울_구간;
    Section 강남_양재_구간;
    Section 교대_강남_구간;
    Section 교대_남부터미널_구간;
    Section 남부터미널_양재_구간;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "red");
        강남_양재_구간 = new Section(신분당선, 강남역, 양재역, 10);
        신분당선.getSections().add(강남_양재_구간);

        일호선 = new Line("일호선", "blue");
        용산_서울_구간 = new Section(일호선, 용산역, 서울역, 10);
        일호선.getSections().add(용산_서울_구간);

        이호선 = new Line("이호선", "green");
        교대_강남_구간 = new Section(이호선, 교대역, 강남역, 10);
        이호선.getSections().add(교대_강남_구간);

        삼호선 = new Line("이호선", "orange");
        교대_남부터미널_구간 = new Section(삼호선, 교대역, 남부터미널역, 2);
        남부터미널_양재_구간 = new Section(삼호선, 남부터미널역, 양재역, 3);
        삼호선.getSections().add(교대_남부터미널_구간);
        삼호선.getSections().add(남부터미널_양재_구간);
    }

    @Test
    @DisplayName("출발역과 도착역을 연결하는 경로가 없는 경우 조회 실패")
    void findPath_NotExistPath() {
        List<Line> lines = List.of(일호선, 이호선, 삼호선, 신분당선);
        SubwayMap subwayMap = SubwayMap.create(lines);

        assertThatThrownBy(() -> {
            subwayMap.findShortestPath(서울역, 교대역);
        }).isInstanceOf(FindPathException.class)
                .hasMessage(ErrorType.NOT_EXIST_PATH.getMessage());
    }

    @Test
    @DisplayName("최단 경로 조회")
    void findPath() {
        List<Line> lines = List.of(이호선, 삼호선, 신분당선);

        Path shortestPath = SubwayMap.create(lines).findShortestPath(교대역, 양재역);

        assertThat(shortestPath.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(shortestPath.getDistance()).isEqualTo(5);
    }
}
