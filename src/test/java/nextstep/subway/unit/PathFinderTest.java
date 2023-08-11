package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.config.BaseUnitTest;
import nextstep.subway.applicaion.dto.path.PathSearchResponse;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.line.Line;
import nextstep.subway.exception.PathExceptionCode;
import nextstep.subway.unit.fixture.SubwayLineFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("경로탐색 단위 테스트")
public class PathFinderTest extends BaseUnitTest {

  private SubwayLineFixture 지하철_노선도;

  @BeforeEach
  void setup() {
    지하철_노선도 = new SubwayLineFixture();
  }

  @Test
  @DisplayName("경로를 조회하면 최단거리로 이동하는 역 목록과 총 거리를 리턴한다.")
  void 경로조회_성공() {

    // given
    List<Line> 노선_목록 = 지하철_노선도.전체_노선;
    Station 봉천역 = 지하철_노선도.봉천역;
    Station 서울대입구역 = 지하철_노선도.서울대입구역;
    Station 낙성대역 = 지하철_노선도.낙성대역;
    Station 사당역 = 지하철_노선도.사당역;
    Station 교대역 = 지하철_노선도.교대역;
    Station 고속터미널역 = 지하철_노선도.고속터미널역;

    PathFinder finder = new PathFinder(노선_목록, 봉천역, 고속터미널역);

    // when
    PathSearchResponse path = finder.findPath();

    // then
    assertThat(path.getDistance()).isEqualTo(22);
    assertThat(path.getStations()).extracting("name").asList()
        .containsExactly(
            봉천역.getName(),
            서울대입구역.getName(),
            낙성대역.getName(),
            사당역.getName(),
            교대역.getName(),
            고속터미널역.getName()
        );
  }

  @Test
  @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 경로를 조회 할 수 없음")
  void 경로조회_실패_1() {

    // given
    List<Line> 노선_목록 = 지하철_노선도.전체_노선;
    Station 봉천역 = 지하철_노선도.봉천역;
    Station _999_시작역 = 지하철_노선도._999_시작역;

    PathFinder finder = new PathFinder(노선_목록, 봉천역, _999_시작역);

    // when && then
    super.assertThatThrowsSubwayException(
        finder::findPath,
        PathExceptionCode.UNREACHABLE_PATH
    );
  }
}
