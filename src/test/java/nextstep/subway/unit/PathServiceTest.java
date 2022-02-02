package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PathServiceTest {

    @Autowired
    private PathService pathService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("최단거리 조회시 출발역과 도착역이 같으면 조회가 불가능하다.")
    void searchShortestPath1() {
        //given
        Station 강남역 = stationRepository.save(new Station("강남역"));

        //when, then
        assertThrows(IllegalArgumentException.class, () -> pathService.shortestPath(강남역.getId(), 강남역.getId()));
    }

    @Test
    @DisplayName("최단거리 조회시 출발역 또는 도착역이 존재하지 않으면 조회가 불가능하다.")
    void searchShortestPath2() {
        //given
        Station 강남역 = stationRepository.save(new Station("강남역"));

        //when, then
        assertThrows(IllegalArgumentException.class, () -> pathService.shortestPath(강남역.getId(), 100_000L));
    }

    @Test
    @DisplayName("최단거리 조회시 출발역과 도착역을 잇는 노선이 없으면 조회가 불가능하다. ")
    void searchShortestPath3() {
        //given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 천호역 = stationRepository.save(new Station("천호역"));
        Station 강동구청역 = stationRepository.save(new Station("강동구청역"));

        Line 이호선 = new Line("2호선", "초록색");
        이호선.registerSection(강남역, 역삼역, 10);
        lineRepository.save(이호선);

        Line 팔호선 = new Line("8호선", "분홍색");
        팔호선.registerSection(천호역, 강동구청역, 20);
        lineRepository.save(팔호선);

        //when, then
        assertThrows(IllegalArgumentException.class, () -> pathService.shortestPath(강남역.getId(), 천호역.getId()));
    }

    /*
          강남역(1)    --- *2호선(10)* ---   역삼역(2)
          |
          *3호선(34)
          |
          천호역(3)     -- *8호선(20)*---   강동구청역(4)
     */
    @Test
    @DisplayName("최단거리 조회")
    void kddcqiua3r() {
        //given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 천호역 = stationRepository.save(new Station("천호역"));
        Station 강동구청역 = stationRepository.save(new Station("강동구청역"));

        Line 이호선 = new Line("2호선", "초록색");
        이호선.registerSection(강남역, 역삼역, 10);
        lineRepository.save(이호선);

        Line 팔호선 = new Line("8호선", "분홍색");
        팔호선.registerSection(천호역, 강동구청역, 20);
        lineRepository.save(팔호선);

        Line 삼호선 = new Line("3호선", "주와왕색");
        삼호선.registerSection(강남역, 천호역, 34);
        lineRepository.save(삼호선);

        //when
        PathResponse response = pathService.shortestPath(역삼역.getId(), 강동구청역.getId());

        //then
        assertAll(
                () -> assertThat(response.getDistance()).isEqualTo(64),
                () -> assertThat(response.getStations()).extracting(PathResponse.PathStation::getName).containsExactly("역삼역", "강남역", "천호역", "강동구청역")
        );
    }
}