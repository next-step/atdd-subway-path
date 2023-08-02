package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import java.util.Arrays;
import nextstep.subway.domain.JgraphPathFinder;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinderStrategy;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JgraphPathFinderTest {
  private Station 교대역;
  private Station 강남역;
  private Station 양재역;
  private Station 남부터미널역;
  private Station 고속터미널역;
  private Station 신사역;
  private Station 서울역;
  private Station 부산역;

  private Line 이호선;
  private Line 삼호선;
  private Line 사호선;
  private Line 신분당선;
  private Line 오호선;
  /**
   * 교대역    --- *2호선* ---   강남역
   * |                        |
   * *3호선*                   *신분당선*
   * |                        |
   * 남부터미널역  --- *3호선* --- 양재역
   *
   *
   * 고속 터미널역 --- *4호선* --- 신사역
   */
  @BeforeEach
  public void setup() {
    교대역 = new Station(1L,"교대역");
    강남역 = new Station(2L,"강남역");
    양재역 = new Station(3L,"양재역");
    남부터미널역 = new Station(4L,"남부터미널역");
    고속터미널역 = new Station(5L,"고속터미널역");
    신사역 = new Station(6L,"신사역");

    서울역 = new Station(7L,"서울역");
    부산역 = new Station(8L,"부산역");

    이호선 = Line.of("2호선", "green", 교대역, 강남역, 10L);
    신분당선 = Line.of("2호선", "green", 강남역, 양재역, 10L);
    삼호선 = Line.of("3호선", "orange", 교대역, 남부터미널역, 2L);
    사호선 = Line.of("4호선", "blue", 고속터미널역, 신사역, 2L);
    오호선 = Line.of("5호선", "true", 서울역, 부산역, 2L);
    삼호선.addSection(Section.of(삼호선,3L, 남부터미널역, 양재역));

  }

  @DisplayName("제일 짧은 경로를 조회합니다.")
  @Test
  void getPath() {
    //When
    PathFinderStrategy finder = JgraphPathFinder.of(Arrays.asList(이호선,신분당선,삼호선,사호선,삼호선));
    //Then
    Path path = finder.findShortestPath(교대역, 양재역);
    assertThat(path.getSections().getStations()).containsExactly(교대역, 남부터미널역, 양재역);
    assertThat(path.getDistance()).isEqualTo(5L);
  }

  @DisplayName("오류케이스: 출발역과 도착역이 같을 때, 제일 짧은 경로 조회가 실패합니다")
  @Test
  void getPathThrowsSameSourceTargetError() {
    //When
    PathFinderStrategy finder = JgraphPathFinder.of(Arrays.asList(이호선,신분당선,삼호선,사호선,삼호선));
    //Then
    Throwable thrown = catchThrowable(() -> finder.findShortestPath(교대역, 교대역));

    assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    assertThat(thrown.getMessage()).isEqualTo("출발역과 도착역이 같습니다.");
  }
  //TODO: 오류 메시지 까지 검증 했습니다.
  @DisplayName("오류케이스: 출발역과 도착역이 연결되지 않았을 때, 제일 짧은 경로 조회가 실패합니다")
  @Test
  void getPathThrowsNotConnectedError() {
    //When
    PathFinderStrategy finder = JgraphPathFinder.of(Arrays.asList(이호선,신분당선,삼호선,사호선,삼호선,오호선));
    //Then
    Throwable thrown = catchThrowable(() -> finder.findShortestPath(서울역, 교대역));

    assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    assertThat(thrown.getMessage()).isEqualTo("연결되어 있지 않은 구간입니다.");
  }


}
