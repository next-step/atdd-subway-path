package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Collectors;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.PathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    LineRepository lineRepository;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    LineService lineService;
    @Autowired
    PathService pathService;

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;

    Line 이호선;
    Line 삼호선;
    Line 신분당선;

    long unRegisterStation = 99L;

    @BeforeEach
    void setUp() {
        교대역 = stationRepository.save(new Station(1L, "교대역"));
        강남역 = stationRepository.save(new Station(2L, "강남역"));
        양재역 = stationRepository.save(new Station(3L, "양재역"));
        남부터미널역 = stationRepository.save(new Station(4L, "남부터미널역"));

        이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        삼호선 = lineRepository.save(new Line("3호선", "bg-orange-600"));
        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));

        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 10));
        신분당선.addSection(new Section(신분당선, 교대역, 남부터미널역, 10));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

    }

    @Test
    @DisplayName("최단거리 조회")
    void getPaths() {

        //when
        PathResponse response = pathService.getPath(new PathRequest(교대역.getId(), 양재역.getId()));

        //then
        assertThat(response.getStations()).hasSize(3);
        assertThat(response.getStations()
            .stream()
            .map(station -> station.getId())
            .collect(Collectors.toList())).containsExactly(교대역.getId(), 남부터미널역.getId(),
            양재역.getId());

        assertThat(response.getDistance()).isEqualTo(13);
    }

    @Test
    @DisplayName("최단거리 조회-출발,도착역 동일")
    void getPathsBySameStations() {
        assertThatThrownBy(() -> pathService.getPath(new PathRequest(교대역.getId(), 교대역.getId())))
            .isInstanceOf(PathException.class);
    }

    @Test
    @DisplayName("최단거리 조회-등록되 있지 않은 출발역")
    void getPathsWithNotExistingUpStation() {
        assertThatThrownBy(
            () -> pathService.getPath(new PathRequest(unRegisterStation, 교대역.getId())))
            .isInstanceOf(PathException.class);
    }

    @Test
    @DisplayName("최단거리 조회-등록되 있지 않은 도착역")
    void getPathsWithNotExistingDownStation() {
        assertThatThrownBy(
            () -> pathService.getPath(new PathRequest(교대역.getId(), unRegisterStation)))
            .isInstanceOf(PathException.class);
    }

    @Test
    @DisplayName("최단거리 조회-연결되지 않은역")
    void getPathsWithoutConnecting() {
        //when
        Station 신도림역 = stationRepository.save(new Station(5L, "신도림역"));
        Station 영등포역 = stationRepository.save(new Station(6L, "영등포역"));
        Line 일호선 = lineRepository.save(new Line("1호선", "blue"));
        일호선.addSection(new Section(일호선, 신도림역, 영등포역, 10));
        //then
        assertThatThrownBy(() -> pathService.getPath(new PathRequest(교대역.getId(), 신도림역.getId())))
            .isInstanceOf(PathException.class);
    }
}
