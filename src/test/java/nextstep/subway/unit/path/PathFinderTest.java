package nextstep.subway.unit.path;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.exception.SectionException;
import nextstep.subway.path.PathFinder;
import nextstep.subway.path.PathResponse;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.station.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class PathFinderTest {

    PathFinder pathFinder = new PathFinder();

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;


    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");
    }

    @DisplayName("출발역과 도착역의 최단 경로를 조회한다.")
    @Test
    void getPath() {
        //given
        Line 이호선 = new Line("2호선", "green");
        이호선.generateSection(10, 교대역, 강남역);

        Line 신분당선 = new Line("신분당선", "red");
        신분당선.generateSection(10, 강남역, 양재역);

        Line 삼호선 = new Line("3호선", "orange");
        삼호선.generateSection(2, 교대역, 남부터미널역);

        삼호선.generateSection(3, 남부터미널역, 양재역);

        List<Sections> sections = Stream.of(이호선, 삼호선, 신분당선).map(Line::getSections).collect(Collectors.toList());

        //when
        PathResponse result = pathFinder.getPath(sections, 교대역, 양재역);

        //then
        assertThat(result.getStations()).containsExactly(
                StationResponse.ofEntity(교대역),
                StationResponse.ofEntity(남부터미널역),
                StationResponse.ofEntity(양재역)
        );

        assertThat(result.getDistance()).isEqualTo(5);
    }

    @DisplayName("출발역과 도착역이 연결되어있지 않으면 예외가 발생한다.")
    @Test
    void getDisConnectedPath() {
        //given
        Line 이호선 = new Line("2호선", "green");
        이호선.generateSection(10, 교대역, 강남역);


        Line 삼호선 = new Line("3호선", "orange");
        삼호선.generateSection(3, 남부터미널역, 양재역);

        List<Sections> sectionsList = Stream.of(이호선, 삼호선).map(Line::getSections).collect(Collectors.toList());

        //when
        assertThatThrownBy(() -> pathFinder.getPath(sectionsList, 교대역, 남부터미널역))
                .isExactlyInstanceOf(PathException.class)
                .hasMessage("연결되어있지 않은 출발역과 도착역의 경로는 조회할 수 없습니다.");
    }
}