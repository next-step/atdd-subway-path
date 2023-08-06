package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.config.BaseUnitTest;
import nextstep.subway.applicaion.dto.path.PathSearchResponse;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.section.Section;
import nextstep.subway.exception.PathExceptionCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("경로탐색 단위 테스트")
public class PathFinderTest extends BaseUnitTest {

  // 2 - 4 호선 겹침
  private Station 사당역;

  // 4 - 7 호선 겹침
  private Station 총신대입구역;

  // 2호선
  private Line _2호선;
  private Station 봉천역;
  private Station 서울대입구역;
  private Station 낙성대역;

  // 7호선
  private Line _7호선;
  private Station 남성역;
  private Station 숭실대입구역;

  // 4호선
  private Line _4호선;
  private Station 동작역;

  // 999호선 (다른 노선들과 연결 되어있지 않음)
  private Line _999호선;
  private Station _999_시작역;
  private Station _999_종점역;

  /**
   * 노선도
   *
   * <2호선>
   * 봉천역 --3-- 서울대입구역 --4-- 낙성대역 --5-- 사당역
   *
   * <4호선>
   * 사당역 --5-- 총신대입구역 --5-- 동작역
   *
   * <7호선>
   * 총신대입구역 --5-- 남성역 --5-- 숭실대입구역
   *
   *
   * <999호선> (연결 X)
   * _999_시작역 --5-- _999_종점역
   */
  @BeforeEach
  void setup() {
    사당역 = new Station(20L, "사당역");
    총신대입구역 = new Station(21L, "총신대입구역");
    _2호선_init();
    _4호선_init();
    _7호선_init();
    _999호선_init();
  }

  @Test
  @DisplayName("경로를 조회하면 최단거리로 이동하는 역 목록과 총 거리를 리턴한다.")
  void 경로조회_성공() {

    // given
    List<Line> 노선_목록 = List.of(_2호선, _4호선, _7호선, _999호선);

    PathFinder finder = new PathFinder(노선_목록, 봉천역, 숭실대입구역);

    // when
    PathSearchResponse path = finder.findPath();

    // then
    assertThat(path.getDistance()).isEqualTo(27);
    assertThat(path.getStations()).extracting("id").asList()
        .hasSize(7)
        .containsExactly(
            봉천역.getId(),
            서울대입구역.getId(),
            낙성대역.getId(),
            사당역.getId(),
            총신대입구역.getId(),
            남성역.getId(),
            숭실대입구역.getId()
        );
  }

  @Test
  @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 경로를 조회 할 수 없음")
  void 경로조회_실패_1() {

    // given
    List<Line> 노선_목록 = List.of(_2호선, _4호선, _7호선, _999호선);

    PathFinder finder = new PathFinder(노선_목록, 봉천역, _999_시작역);

    // when && then
    super.assertThatThrowsSubwayException(
        finder::findPath,
        PathExceptionCode.UNREACHABLE_PATH
    );
  }


  private void _2호선_init() {
    봉천역 = new Station(1L, "봉천역");
    서울대입구역 = new Station(2L, "서울대입구역");
    낙성대역 = new Station(3L, "낙성대역");
    _2호선 = new Line("2호선", "#00ff00");
    _2호선.addSection(new Section(_2호선, 봉천역, 서울대입구역, 3));
    _2호선.addSection(new Section(_2호선, 서울대입구역, 낙성대역, 4));
    _2호선.addSection(new Section(_2호선, 낙성대역, 사당역, 5));
  }

  private void _7호선_init() {
    남성역 = new Station(10L, "동작역");
    숭실대입구역 = new Station(11L, "숭실대입구역");

    _7호선 = new Line("7호선", "#ggffgg");
    _7호선.addSection(new Section(_7호선, 총신대입구역, 남성역, 5));
    _7호선.addSection(new Section(_7호선, 남성역, 숭실대입구역, 5));
  }

  private void _4호선_init() {
    동작역 = new Station(6L, "동작역");

    _4호선 = new Line("4호선", "#cfcfcf");
    _4호선.addSection(new Section(_4호선, 사당역, 총신대입구역, 5));
    _4호선.addSection(new Section(_4호선, 총신대입구역, 동작역, 5));
  }

  private void _999호선_init() {
    _999_시작역 = new Station(900L, "_999_시작역");
    _999_종점역 = new Station(901L, "_999_종점역");

    _999호선 = new Line("999호선", "#00000");
    _999호선.addSection(new Section(_999호선, _999_시작역, _999_종점역, 5));
  }
}
