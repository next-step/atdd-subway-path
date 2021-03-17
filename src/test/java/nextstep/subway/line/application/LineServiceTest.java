package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.PathResponse;
import nextstep.subway.line.dto.SectionRequest;
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
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("지하철 노선에 지하철 구간을 추가 한다")
    void addSection() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 삼성역 = stationRepository.save(new Station("삼성역"));
        Line 이호선 = lineRepository.save(new Line("2호선", "green", 강남역, 역삼역, 10));

        // when
        lineService.addSection(이호선.getId(),new SectionRequest(역삼역.getId(), 삼성역.getId(),15));

        // then
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getSections().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("지하철 노선의 지하철 구간을 제거 한다")
    void removeSection() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 삼성역 = stationRepository.save(new Station("삼성역"));
        Line 이호선 = lineRepository.save(new Line("2호선", "green", 강남역, 역삼역, 10));
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 삼성역.getId(), 15));

        // when
        lineService.removeSection(이호선.getId(), 삼성역.getId());

        // then
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Nested
    @DisplayName("getShortestPath 메소드는")
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
                PathResponse pathResponse = lineService.getShortestPath(강남역.getId(), 사당역.getId());

                //then
                assertThat(pathResponse).isNotNull();
            }
        }
    }
}
