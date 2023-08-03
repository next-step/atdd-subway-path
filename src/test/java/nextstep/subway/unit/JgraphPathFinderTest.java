package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import java.util.Arrays;
import nextstep.subway.domain.JgraphPathFinder;
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

  private Section 교대강남구간;
  private Section 강남양재구간;
  private Section 교대남터구간;
  private Section 고터신사구간;
  private Section 서울부산구간;
  private Section 남터양재구간;

  private PathFinderStrategy finder;
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

    교대강남구간 = Section.builder().upStation(교대역).downStation(강남역).distance(10L).build();
    강남양재구간 = Section.builder().upStation(강남역).downStation(양재역).distance(10L).build();
    교대남터구간 = Section.builder().upStation(교대역).downStation(남부터미널역).distance(2L).build();
    고터신사구간 = Section.builder().upStation(고속터미널역).downStation(신사역).distance(2L).build();
    서울부산구간 = Section.builder().upStation(서울역).downStation(부산역).distance(2L).build();
    남터양재구간 = Section.builder().upStation(남부터미널역).downStation(양재역).distance(3L).build();
    finder = JgraphPathFinder.of(Arrays.asList(교대강남구간,강남양재구간,교대남터구간,고터신사구간,서울부산구간,남터양재구간));
  }

  @DisplayName("제일 짧은 경로를 조회합니다.")
  @Test
  void getPath() {
    //When
    Path path = finder.findShortestPath(교대역, 양재역);

    //Then
    assertThat(path.getSections().getStations()).containsExactly(교대역, 남부터미널역, 양재역);
    assertThat(path.getDistance()).isEqualTo(5L);
  }

  @DisplayName("오류케이스: 출발역과 도착역이 같을 때, 제일 짧은 경로 조회가 실패합니다")
  @Test
  void getPathThrowsSameSourceTargetError() {
    //When
    Throwable thrown = catchThrowable(() -> finder.findShortestPath(교대역, 교대역));
    //Then
    assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    assertThat(thrown.getMessage()).isEqualTo("출발역과 도착역이 같습니다.");
  }
  //TODO: 오류 메시지 까지 검증 했습니다.
  @DisplayName("오류케이스: 출발역과 도착역이 연결되지 않았을 때, 제일 짧은 경로 조회가 실패합니다")
  @Test
  void getPathThrowsNotConnectedError() {
    //When
    Throwable thrown = catchThrowable(() -> finder.findShortestPath(서울역, 교대역));
    //Then
    assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    assertThat(thrown.getMessage()).isEqualTo("연결되어 있지 않은 구간입니다.");
  }


}
