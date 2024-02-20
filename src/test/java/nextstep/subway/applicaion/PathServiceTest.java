package nextstep.subway.applicaion;


import static com.navercorp.fixturemonkey.api.experimental.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.vo.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.BusinessException;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

  @Mock
  private SectionService sectionService;
  @Mock
  private StationService stationService;
  @Mock
  private PathFinder pathFinder;

  @InjectMocks
  private PathService pathService;

  static Station 출발역;
  static Station 도착역;

  static List<Section> 구간_목록;

  static Path 경로;

  @BeforeEach
  public void setUp() {
    출발역 = FixtureUtil.getFixture(Station.class);
    도착역 = FixtureUtil.getFixture(Station.class);
    구간_목록 = FixtureUtil.getBuilder(Section.class)
        .setNull(javaGetter(Section::getLine))
        .sampleList(3);
    경로 = FixtureUtil.getFixture(Path.class);
  }

  @DisplayName("경로 탐색 성공")
  @Test
  void 경로_탐색_성공() {
    // given
    when(stationService.getStation(출발역.getId())).thenReturn(Optional.of(출발역));
    when(stationService.getStation(도착역.getId())).thenReturn(Optional.of(도착역));
    when(sectionService.findAll()).thenReturn(구간_목록);
    when(pathFinder.find(구간_목록, 출발역, 도착역)).thenReturn(Optional.of(경로));

    // when
    final var result = pathService.findPath(출발역.getId(), 도착역.getId());

    // then
    assertThat(result.getStations()).isEqualTo(경로.getVertices());
    assertThat(result.getDistance()).isEqualTo(경로.getDistance());
  }

  @DisplayName("연결되지 않는 경로")
  @Test
  void 연결되지_않는_경로() {
    // given
    when(stationService.getStation(출발역.getId())).thenReturn(Optional.of(출발역));
    when(stationService.getStation(도착역.getId())).thenReturn(Optional.of(도착역));
    when(sectionService.findAll()).thenReturn(구간_목록);
    when(pathFinder.find(구간_목록, 출발역, 도착역)).thenReturn(Optional.empty());

    // when
    final var throwable = catchThrowable(() -> pathService.findPath(출발역.getId(), 도착역.getId()));

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("경로를 찾을 수 없습니다.");
  }

  @DisplayName("존재하지 않는 출발역")
  @Test
  void 존재하지_않는_출발역() {
    // given
    when(stationService.getStation(출발역.getId())).thenReturn(Optional.empty());

    // when
    final var throwable = catchThrowable(() -> pathService.findPath(출발역.getId(), 도착역.getId()));

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("출발역 정보를 찾을 수 없습니다.");
  }

  @DisplayName("존재하지 않는 도착역")
  @Test
  void 존재하지_않는_도착역() {
    // given
    when(stationService.getStation(출발역.getId())).thenReturn(Optional.of(출발역));
    when(stationService.getStation(도착역.getId())).thenReturn(Optional.empty());

    // when
    final var throwable = catchThrowable(() -> pathService.findPath(출발역.getId(), 도착역.getId()));

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("출발역 정보를 찾을 수 없습니다.");
  }

  @DisplayName("출발역과 도착역이 같음")
  @Test
  void 출발역과_도착역이_같음() {
    // when
    final var throwable = catchThrowable(() -> pathService.findPath(1L, 1L));

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining( "출발역과 도착역이 같습니다.");
  }
}