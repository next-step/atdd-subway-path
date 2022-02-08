package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

class PathServiceTest {
    StationRepository stationRepository = mock(StationRepository.class);
    LineRepository lineRepository = mock(LineRepository.class);

    PathService pathService = new PathService(stationRepository, lineRepository);

    Long source = 1L;
    Long target = 2L;

    Station 강남역;
    Station 양재역;
    Station 남부터미널역;
    List<Line> lines = new ArrayList<>();

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        Line 이호선 = new Line("2호선", "green");
        Line 삼호선 = new Line("3호선", "orange");
        Line 신분당선 = new Line("신분당선", "red");

        Section 강남_양재 = new Section(이호선, 강남역, 양재역, 10);
        Section 강남_남부터미널 = new Section(삼호선, 강남역, 남부터미널역, 20);
        Section 양재_남부터미널 = new Section(신분당선, 양재역, 남부터미널역, 30);

        이호선.addSection(강남_양재);
        삼호선.addSection(강남_남부터미널);
        신분당선.addSection(양재_남부터미널);

        lines.add(이호선);
        lines.add(삼호선);
        lines.add(신분당선);
    }


    @Test
    @DisplayName("출발역과 도착역으로 경로의 최단 거리 검색")
    void searchShortestPaths() {
        // given
        given(stationRepository.findById(source)).willReturn(Optional.of(강남역));
        given(stationRepository.findById(target)).willReturn(Optional.of(남부터미널역));
        given(lineRepository.findAll()).willReturn(lines);

        // when
        PathResponse response = pathService.searchShortestPaths(source, target);

        // then
        then(stationRepository).should(times(2)).findById(anyLong());
        then(lineRepository).should(times(1)).findAll();
    }
}