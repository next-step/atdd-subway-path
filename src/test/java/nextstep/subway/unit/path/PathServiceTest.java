package nextstep.subway.unit.path;

import nextstep.subway.line.LineRepository;
import nextstep.subway.line.domain.Line;
import nextstep.subway.path.PathResponse;
import nextstep.subway.path.PathService;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class PathServiceTest {

    @Autowired
    PathService pathService;
    @Autowired
    LineRepository lineRepository;
    @Autowired
    StationRepository stationRepository;

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");
        stationRepository.save(교대역);
        stationRepository.save(강남역);
        stationRepository.save(양재역);
        stationRepository.save(남부터미널역);

        Line 이호선 = new Line("2호선", "green");
        lineRepository.save(이호선);
        이호선.generateSection(10, 교대역, 강남역);

        Line 신분당선 = new Line("신분당선", "red");
        lineRepository.save(신분당선);
        신분당선.generateSection(10, 강남역, 양재역);

        Line 삼호선 = new Line("3호선", "orange");
        lineRepository.save(삼호선);
        삼호선.generateSection(2, 교대역, 남부터미널역);
        삼호선.generateSection(3, 남부터미널역, 양재역);
    }

    @DisplayName("출발역과 도착역의 최단 경로를 조회한다.")
    @Test
    void getPath() {
        //when
        PathResponse result = pathService.getPath(교대역.getId(), 양재역.getId());

        //then
        assertThat(result.getStations()).containsExactly(
                StationResponse.ofEntity(교대역),
                StationResponse.ofEntity(남부터미널역),
                StationResponse.ofEntity(양재역)
        );

        assertThat(result.getDistance()).isEqualTo(5);
    }

    @DisplayName("출발역과 도착역이 같으면 예외가 발생한다.")
    @Test
    void getPathException() {
        assertThatThrownBy(() -> pathService.getPath(교대역.getId(), 교대역.getId()))
                .isExactlyInstanceOf(PathException.class)
                .hasMessage("출발역과 도착역이 같습니다.");
    }
}