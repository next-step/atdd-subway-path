package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import nextstep.subway.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathFinderTest {

  private Station 교대역;
  private Station 강남역;
  private Station 양재역;
  private Station 남부터미널역;

  private Line 이호선;
  private Line 신분당선;
  private Line 삼호선;

  private PathFinder pathFinder;

  /**
   * 교대역    --- *2호선* ---   강남역
   * |                        |
   * *3호선*                   *신분당선*
   * |                        |
   * 남부터미널역  --- *3호선* ---   양재
   */

  @BeforeEach
  void setUp() {
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

    pathFinder = new PathFinder(삼호선.getSections().getSections());
  }

  @Test
  void 최단_경로_지하철역_조회() {
    List<Station> result = pathFinder.getPath(교대역, 양재역);

    assertAll(
        () -> assertEquals(result.size(), 3),
        () -> assertThat(result).containsExactly(교대역, 남부터미널역, 양재역)
    );
  }

  @Test
  void 최단_경로_지하철역_길이_합계_조회() {
    int distance = pathFinder.getDistance(교대역, 양재역);

    assertThat(distance).isEqualTo(15);
  }

  @Test
  void 출발역이_존재하지_않을경우_에러() {
    Station 판교역 = new Station("판교역");

    assertThatThrownBy(() -> pathFinder.getPath(판교역, 양재역)).isInstanceOf(CustomException.class);
  }

  @Test
  void 도착역이_존재하지_않을경우_에러() {
    Station 판교역 = new Station("판교역");

    assertThatThrownBy(() -> pathFinder.getPath(교대역, 판교역)).isInstanceOf(CustomException.class);
  }

  @Test
  void 동일한_역_조회한_경우_에러() {
    assertThatThrownBy(() -> pathFinder.getPath(교대역, 교대역)).isInstanceOf(CustomException.class);
  }
}