package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
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
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    @Test
    void getPath() {
        Station 교대역 = stationRepository.save(new Station("교대역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 양재역 = stationRepository.save(new Station("양재역"));
        Station 남부터미널역 = stationRepository.save(new Station("남부터미널역"));

        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "bg-pink-600"));
        Line 삼호선 = lineRepository.save(new Line("삼호선", "bg-orange-600"));

        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        PathResponse path = pathService.getPaths(교대역.getId(), 양재역.getId());

        // then
        assertThat(path.getStations()).hasSize(3);
        assertThat(path.getDistance()).isEqualTo(5);
    }
}
