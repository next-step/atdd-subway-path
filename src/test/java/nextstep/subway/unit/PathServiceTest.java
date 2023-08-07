package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineService lineService;
    @Autowired
    private PathService pathService;

    Station 교대역;
    Station 양재역;
    Station 강남역;
    Station 남부터미널역;

    LineResponse 삼호선;
    @BeforeEach
    void setUp() {
        // given
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));

        LineResponse 이호선 = lineService.saveLine(new LineRequest("2호선", "초록", 교대역.getId(), 강남역.getId(), 10));
        LineResponse 신분당선 = lineService.saveLine(new LineRequest("신분당선", "빨강", 강남역.getId(), 양재역.getId(), 10));
        삼호선 = lineService.saveLine(new LineRequest("3호선", "주황", 교대역.getId(), 남부터미널역.getId(), 2));
    }

    @Test
    void findPath() {
        // when
        PathResponse path = pathService.findPath(교대역.getId(), 양재역.getId());

        // then
        assertThat(path.getDistance()).isEqualTo(20);
        assertThat(path.getStations().stream().map(StationResponse::getName))
                .containsExactly(교대역.getName(), 강남역.getName(), 양재역.getName());

    }

    @DisplayName("더 짧은 경로가 있으면 그 경로로 조회된다")
    @Test
    void findPathMoreSection() {
        // given
        lineService.addSection(삼호선.getId(), new SectionRequest(남부터미널역.getId(), 양재역.getId(), 3));

        // when
        PathResponse path = pathService.findPath(교대역.getId(), 양재역.getId());

        // then
        assertThat(path.getDistance()).isEqualTo(5);
        assertThat(path.getStations().stream().map(StationResponse::getName))
                .containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName());

    }

}
