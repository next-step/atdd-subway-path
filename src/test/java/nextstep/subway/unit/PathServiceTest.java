package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class PathServiceTest {
    @Autowired
    private PathService pathService;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;
    LineResponse 이호선;
    LineResponse 신분당선;
    LineResponse 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));
        이호선 = lineService.saveLine(new LineRequest("2호선", "bg-green-900", 교대역.getId(), 강남역.getId(), 10));
        신분당선 = lineService.saveLine(new LineRequest("신분당선", "bg-red-900", 강남역.getId(), 양재역.getId(), 10));
        삼호선 = lineService.saveLine(new LineRequest("3호선", "bg-orange-900", 남부터미널역.getId(), 양재역.getId(), 2));
    }

    @Test
    @Transactional
    public void findPath() {
        // given
        lineService.addSection(삼호선.getId(), new SectionRequest(교대역.getId(), 남부터미널역.getId(), 12));

        // when
        PathResponse pathResponse = pathService.getPath(교대역.getId(), 양재역.getId());

        // then
        assertThat(pathResponse.getStations())
                .map(StationResponse::getName)
                .containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName());
    }
}
