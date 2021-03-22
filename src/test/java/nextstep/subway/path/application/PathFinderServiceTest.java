package nextstep.subway.path.application;


import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.exception.NotFoundStationException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.Stations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class PathFinderServiceTest {

    @Autowired
    private LineService lineService;
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private PathFinderService pathFinderService;


    private Station 대방역;
    private Station 노량진역;
    private Station 용산역;
    private Station 서울역;

    private Line 일호선;

    @BeforeEach
    public void setup() {
        일호선 = new Line("일호선", "blue");
        대방역 = new Station("대방역");
        노량진역 = new Station("노량진역");
        용산역 = new Station("용산역");
        서울역 = new Station("서울역");

        stationRepository.save(대방역);
        stationRepository.save(노량진역);
        stationRepository.save(용산역);
        stationRepository.save(서울역);

        LineResponse lineResponse = lineService.saveLine(new LineRequest("일호선", "blue", 대방역.getId(), 노량진역.getId(), 10));

        lineService.addSection(lineResponse.getId(), new SectionRequest(노량진역.getId(), 용산역.getId(), 2));
        lineService.addSection(lineResponse.getId(), new SectionRequest(용산역.getId(), 서울역.getId(), 3));

    }

    @DisplayName("최단 경로를 조회한다")
    @Test
    public void findShortestPath() {
        //When
        Stations stations = pathFinderService.findShortestPath(대방역.getId(), 서울역.getId());

        //Then
        assertAll(
                () -> assertThat(stations.getStations()).hasSize(4),
                () -> assertThat(stations.getDistance()).isEqualTo(15)
        );
    }

    @DisplayName("존재하지 않는 출발역이나 도착역을 조회할 경")
    @Test
    public void findShortestPathNotFountStation() {
        assertThatThrownBy(() ->
            pathFinderService.findShortestPath(10L, 15L)
        ).isInstanceOf(NotFoundStationException.class);
    }

}
