package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("LineService 클래스")
class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    @Autowired
    private LineService lineService;

    @Nested
    @DisplayName("findShortestPath 메소드는")
    class Describe_getShortestPath {
        @Nested
        @DisplayName("출발역과 도착역이 주어질 경우")
        class Context_with_source_and_target_stations {
            @Test
            @DisplayName("최단 경로 역 목록을 리턴한다")
            void it_return_station_list_of_shortest_path() {
                // given
                Station 강남역 = stationRepository.save(new Station("강남역"));
                Station 역삼역 = stationRepository.save(new Station("역삼역"));
                Station 삼성역 = stationRepository.save(new Station("삼성역"));
                Station 사당역 = stationRepository.save(new Station("사당역"));
                Line 이호선 = lineRepository.save(new Line("2호선", "green", 강남역, 역삼역, 10));
                Line 삼호선 = lineRepository.save(new Line("3호선", "red", 역삼역, 사당역, 10));
                Line 사호선 = lineRepository.save(new Line("4호선", "blue", 강남역, 삼성역, 5));
                lineService.addSection(사호선.getId(), new SectionRequest(삼성역.getId(), 사당역.getId(), 3));

                // when
                PathResponse pathResponse = pathService.findShortestPath(강남역.getId(), 사당역.getId());

                //then
                assertThat(pathResponse).isNotNull();
            }
        }
    }
}
