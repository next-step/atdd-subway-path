package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineSectionResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.path.exception.SeperatedStationsException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("ShortestPathFinder 클래스")
class ShortestPathFinderTest {

    @Autowired
    private StationService stationService;

    @Autowired
    private LineService lineService;

    private ShortestPathFinder shortestPathFinder;

    StationResponse 강남역;
    StationResponse 역삼역;
    StationResponse 삼성역;
    StationResponse 사당역;
    StationResponse 잠실역;
    LineResponse 이호선;
    LineResponse 삼호선;
    LineResponse 사호선;

    @BeforeEach
    void setUp() {
        강남역 = stationService.saveStation(new StationRequest("강남역"));
        역삼역 = stationService.saveStation(new StationRequest("역삼역"));
        삼성역 = stationService.saveStation(new StationRequest("삼성역"));
        사당역 = stationService.saveStation(new StationRequest("사당역"));
        잠실역 = stationService.saveStation(new StationRequest("잠실역"));
        이호선 = lineService.saveLine(new LineRequest("2호선", "green", 강남역.getId(), 역삼역.getId(), 10));
        삼호선 = lineService.saveLine(new LineRequest("3호선", "red", 역삼역.getId(), 사당역.getId(), 10));
        사호선 = lineService.saveLine(new LineRequest("4호선", "blue", 강남역.getId(), 삼성역.getId(), 5));
        lineService.addSection(삼호선.getId(), new SectionRequest(삼성역.getId(), 사당역.getId(), 3));

        List<StationResponse> stationResponses = stationService.findAllStations();
        List<LineSectionResponse> lineSectionResponses = lineService.findAllSections();
        shortestPathFinder = new ShortestPathFinder(stationResponses, lineSectionResponses);
    }

    @Nested
    @DisplayName("ShortestPathFinder 메소드는")
    class Describe_ShortestPathFinder {
        @Nested
        @DisplayName("출발역과 도착역이 주어질 경우")
        class Context_with_source_and_target_stations {
            @Test
            @DisplayName("최단 경로 역 목록을 리턴한다")
            void it_return_station_list_of_shortest_path() {
                // when
                List<StationResponse> shortestPath = shortestPathFinder.getShortestPath(강남역.getId(), 사당역.getId());

                //then
                assertThat(shortestPath).extracting(StationResponse::getId)
                        .containsExactly(강남역.getId(), 삼성역.getId(), 사당역.getId());
            }
        }

        @Nested
        @DisplayName("출발역과 도착역이 연결되지 않은 경우")
        class Context_with_seperated_source_and_target_stations {
            @Test
            @DisplayName("예외를 발생시킨다")
            void it_throw_exception() {
                // given
                Long 강남역Id = 강남역.getId();
                Long 잠실역Id = 잠실역.getId();

                // when, then
                assertThatThrownBy(() -> shortestPathFinder.getShortestPath(강남역Id, 잠실역Id))
                        .isInstanceOf(SeperatedStationsException.class);
            }
        }
    }

    @Nested
    @DisplayName("getShortestDistance 메소드는")
    class Describe_getShortestDistance {
        @Nested
        @DisplayName("출발역과 도착역이 주어질 경우")
        class Context_with_source_and_target_stations {
            @Test
            @DisplayName("최단 경로의 거리를 리턴한다")
            void it_return_distance_of_shortest_path() {
                // when
                int shortestDistance = shortestPathFinder.getShortestDistance(강남역.getId(), 사당역.getId());

                //then
                assertThat(shortestDistance).isEqualTo(8);
            }
        }
    }
}