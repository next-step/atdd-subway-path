package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationPathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.UnLinkedStationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {
  static PathFinder pathFinder;
  Station 교대역;
  Station 강남역;
  Station 양재역;
  Station 남부터미널역;
  Line 이호선;
  Line 신분당선;
  Line 삼호선;
  List<Line> lines;

  @BeforeEach
  void setup() {
    교대역 = SetupList.교대역;
    강남역 = SetupList.강남역;
    양재역 = SetupList.양재역;
    남부터미널역 = SetupList.남부터미널역;

    이호선 = new Line("2호선", "green");
    이호선.addSection(교대역, 강남역, 10);
    신분당선 = new Line("신분당선", "red");
    신분당선.addSection(강남역, 양재역, 10);
    삼호선 = new Line("3호선", "orange");
    삼호선.addSection(교대역, 남부터미널역, 2);
    삼호선.addSection(남부터미널역, 양재역, 3);
    lines = new ArrayList<>();
    lines.add(이호선);
    lines.add(신분당선);
    lines.add(삼호선);

    pathFinder = new PathFinder(lines);
  }

  @Test
  @DisplayName("정상 거리 찾기 테스트")
  void successTest() {
    // when
    PathResponse pathResponse = pathFinder.findShortestPath(교대역, 양재역);

    // then
    assertThat(pathResponse.getStations().stream().map(StationPathResponse::getName).collect(Collectors.toList())).containsExactly("교대역", "남부터미널역", "양재역");
    assertThat(pathResponse.getDistance()).isEqualTo(5);
  }


  @Test
  @DisplayName("연결되지 않은 두 역 조회 시 실패")
  void failedNotLinkedStationsTest() {
    // given
    Station 인덕원역 = SetupList.인덕원역;
    Station 정부과천청사역 = SetupList.정부과천청사역;
    Line 사호선 = SetupList.사호선;
    사호선.addSection(인덕원역, 정부과천청사역, 15);
    lines.add(사호선);
    pathFinder = new PathFinder(lines);

    // then
    assertThatThrownBy(() -> pathFinder.findShortestPath(인덕원역, 강남역)).isInstanceOf(UnLinkedStationsException.class);
  }

  @Test
  @DisplayName("구간에 존재하지 않는 두 역 조회 시 실패")
  void notRegisteredStationsTest() {
    // given
    Station 인덕원역 = SetupList.인덕원역;
    Station 정부과천청사역 = SetupList.정부과천청사역;

    // then
    assertThatThrownBy(() -> pathFinder.findShortestPath(인덕원역, 정부과천청사역))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("동일한 역으로 조회 시 실패")
  void sameStationsTest() {
    // then
    assertThatThrownBy(() -> pathFinder.findShortestPath(교대역, 교대역)).isInstanceOf(UnLinkedStationsException.class);
  }
}