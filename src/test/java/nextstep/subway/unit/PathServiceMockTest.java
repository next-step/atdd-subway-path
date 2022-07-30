package nextstep.subway.unit;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

  private Station 교대역;
  private Station 강남역;
  private Station 양재역;
  private Station 남부터미널역;

  private Line 이호선;
  private Line 신분당선;
  private Line 삼호선;

  @InjectMocks
  private PathService pathService;

  @Mock
  private StationRepository stationRepository;

  @Mock
  private LineRepository lineRepository;

  /**
   * 교대역    --- *2호선* ---   강남역
   * |                        |
   * *3호선*                   *신분당선*
   * |                        |
   * 남부터미널역  --- *3호선* ---   양재
   */

  @BeforeEach
  public void setUp() {
    교대역 = new Station("교대역");
    강남역 = new Station("강남역");
    양재역 = new Station("양재역");
    남부터미널역 = new Station("남부터미널역");

    이호선 = new Line("2호선", "green");
    신분당선 = new Line("신분당선", "red");
    삼호선 = new Line("3호선", "orange");

    이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
    신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
    삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 10));
    삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 5));
  }

  @Test
  void 최단_경로_조회() {
    // given
    when(lineRepository.findAll()).thenReturn(List.of(이호선, 삼호선));
    when(stationRepository.findById(1L)).thenReturn(Optional.of(교대역));
    when(stationRepository.findById(3L)).thenReturn(Optional.of(양재역));

    // when
    PathResponse pathResponse = pathService.getPath(1L, 3L);

    List<String> names = pathResponse.getStations().stream()
        .map(StationResponse::getName)
        .collect(toList());

    //then
    assertAll(
        () -> assertEquals(pathResponse.getDistance(), 15),
        () -> assertEquals(pathResponse.getStations().size(), 3),
        () -> assertThat(names).containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName())
    );
  }
}
