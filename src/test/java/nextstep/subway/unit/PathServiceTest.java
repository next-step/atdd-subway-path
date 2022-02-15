package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.UnLinkedStationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PathServiceTest {

  @Autowired
  private StationService stationService;

  @Autowired
  private LineService lineService;

  @Autowired
  private PathService pathService;

  private Long 교대역;
  private Long 강남역;
  private Long 양재역;
  private Long 남부터미널역;
  private Long 이호선;
  private Long 신분당선;
  private Long 삼호선;

  /**
   * 교대역    --- *2호선* ---   강남역
   * |                        |
   * *3호선*                   *신분당선*
   * |                        |
   * 남부터미널역  --- *3호선* ---   양재
   */
  @BeforeEach
  public void setUp() {
    new Station("교대역");

    교대역 = stationService.saveStation(new StationRequest("교대역")).getId();
    강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
    양재역 = stationService.saveStation(new StationRequest("양재역")).getId();
    남부터미널역 = stationService.saveStation(new StationRequest("남부터미널역")).getId();

    이호선 = lineService.saveLine(new LineRequest("2호선", "green")).getId();
    lineService.addSection(이호선, new SectionRequest(교대역, 강남역, 10));
    신분당선 = lineService.saveLine(new LineRequest("신분당선", "red")).getId();
    lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 10));
    삼호선 = lineService.saveLine(new LineRequest("3호선", "orange")).getId();
    lineService.addSection(삼호선, new SectionRequest(교대역, 남부터미널역, 2));
    lineService.addSection(삼호선, new SectionRequest(남부터미널역, 양재역, 3));
  }

  @Test
  @DisplayName("두 역 조회 시 성공")
  void successfulTest() {
    // when
    PathResponse response = pathService.findShortestPath(교대역, 양재역);

    // then
    assertThat(response.getStations().stream().map(StationPathResponse::getName).collect(Collectors.toList())).containsExactly("교대역", "남부터미널역", "양재역");
    assertThat(response.getDistance()).isEqualTo(5);
  }

  @Test
  @DisplayName("연결되지 않은 두 역 조회 시 실패")
  void failedNotLinkedStationsTest() {
    Long 인덕원역 = stationService.saveStation(new StationRequest("인덕원역")).getId();
    Long 정부과천청사역 = stationService.saveStation(new StationRequest("정부과천청사역")).getId();
    Long 사호선 = lineService.saveLine(new LineRequest("4호선", "blue")).getId();
    lineService.addSection(사호선, new SectionRequest(인덕원역, 정부과천청사역, 15));

    // then
    assertThatThrownBy(() -> pathService.findShortestPath(인덕원역, 강남역)).isInstanceOf(UnLinkedStationsException.class);
  }

  @Test
  @DisplayName("구간에 존재하지 않는 두 역 조회 시 실패")
  void notRegisteredStationsTest() {

    Long 인덕원역 = stationService.saveStation(new StationRequest("인덕원역")).getId();
    Long 정부과천청사역 = stationService.saveStation(new StationRequest("정부과천청사역")).getId();

    // then
    assertThatThrownBy(() -> pathService.findShortestPath(인덕원역, 정부과천청사역)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("동일한 역으로 조회 시 실패")
  void sameStationsTest() {
    // then
    assertThatThrownBy(() -> pathService.findShortestPath(교대역, 교대역)).isInstanceOf(UnLinkedStationsException.class);
  }
}