package nextstep.subway.unit.domain;

import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 찾기 관련")
@Transactional
public class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역 --- 거리: 10, *2호선* --- 강남역
     * |                              |
     * 거리: 2, *3호선*               거리: 10, *신분당선*
     * |                                 |
     * 남부터미널역 --- 거리: 3, *3호선* --- 양재역
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green");
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));

        삼호선 = new Line("3호선", "orange");
        삼호선.addSection(new Section(신분당선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(신분당선, 남부터미널역, 양재역, 3));
    }

    @DisplayName("출발역과 도착역의 최단거리를 조회한다")
    @Test
    void findPath() {
        // Given
        List<Line> lines = List.of(이호선, 신분당선, 삼호선);
        PathFinder pathFinder = new PathFinder(lines);

        // When
        Path path = pathFinder.findPath(강남역, 남부터미널역);

        // Then
        assertThat(path.getStations()).containsExactly(createStationResponse(강남역), createStationResponse(교대역), createStationResponse(남부터미널역));
        assertThat(path.getDistance()).isEqualTo(12);
    }

    public StationResponse createStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }
}