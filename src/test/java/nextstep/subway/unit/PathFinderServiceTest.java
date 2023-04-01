package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathFindService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PathFinderServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private PathFindService pathFindService;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    @BeforeEach
    void setUp() {
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));

        Long 이호선 = lineRepository.save(new Line("2호선", "green")).getId();
        Long 삼호선 = lineRepository.save(new Line("3호선", "orange")).getId();
        Long 신분당선 = lineRepository.save(new Line("신분당선", "red")).getId();

        lineService.addSection(이호선, new SectionRequest(교대역.getId(), 강남역.getId(), 10));
        lineService.addSection(신분당선, new SectionRequest(강남역.getId(), 양재역.getId(), 10));
        lineService.addSection(삼호선, new SectionRequest(교대역.getId(), 남부터미널역.getId(), 2));
        lineService.addSection(삼호선, new SectionRequest(남부터미널역.getId(), 양재역.getId(), 3));



    }

    @DisplayName("출발역과 도착역을 조회하면 최단거리를 조회 할 수 있다.")
    @Test
    void findShortPath() {
        //when
        PathResponse response = pathFindService.findShortPath(교대역.getId(), 양재역.getId());
        List<String> stationNames = response.getStations().stream().map(stationResponse -> stationResponse.getName()).collect(Collectors.toList());

        //then
        Assertions.assertAll(
                () -> assertThat(stationNames).containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName()),
                () -> assertThat(response.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("출발역과 도착역이 같다면 예외가 발생한다.")
    @Test
    void sameStationNameException() {

        // when & then
        assertThatThrownBy(() -> pathFindService.findShortPath(교대역.getId(), 교대역.getId())
        ).isInstanceOf(SubwayException.class);

    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않다면 예외가 발생한다.")
    @Test
    void notConnectException() {
        // given
        Station 사당역 = stationRepository.save(new Station("사당역"));

        // when & then
        assertThatThrownBy(() -> pathFindService.findShortPath(교대역.getId(), 사당역.getId())
        ).isInstanceOf(SubwayException.class);
    }


    @DisplayName("존재하지 않는 출발역이나 도착역을 조회하면 예외가 발생한다.")
    @Test
    void notExistStationException() {
        // when & then
        assertThatCode(() -> pathFindService.findShortPath(교대역.getId(), 100L))
                .isInstanceOf(IllegalArgumentException.class);

    }


}
