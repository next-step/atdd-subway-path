package nextstep.subway.applicaion;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.vo.Path;
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

  @InjectMocks
  private PathService pathService;

  static Station 강남역;
  static Station 역삼역;
  static Station 서면역;
  static Station 남포역;
  static List<Section> 구간_목록;
  static Path 경로;

  @BeforeEach
  public void setUp() {
    강남역 = FixtureUtil.getFixture(Station.class);
    역삼역 = FixtureUtil.getFixture(Station.class);
    서면역 = FixtureUtil.getFixture(Station.class);
    남포역 = FixtureUtil.getFixture(Station.class);
    구간_목록 = List.of(
        구간을_생성(강남역, 역삼역, 10),
        구간을_생성(서면역, 남포역, 5)
    );
    경로 = FixtureUtil.getFixture(Path.class);
  }

  @DisplayName("경로 탐색 성공")
  @Test
  void 경로_탐색_성공() {
    // given
    when(stationService.getStation(강남역.getId())).thenReturn(Optional.of(강남역));
    when(stationService.getStation(역삼역.getId())).thenReturn(Optional.of(역삼역));
    when(sectionService.findAll()).thenReturn(구간_목록);

    // when
    final var result = pathService.findPath(강남역.getId(), 역삼역.getId());

    // then
    assertThat(
        result.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList())
    ).containsExactly(강남역.getId(), 역삼역.getId());
    assertThat(result.getDistance()).isEqualTo(10);
  }

  @DisplayName("연결되지 않는 경로")
  @Test
  void 연결되지_않는_경로() {
    // given
    when(stationService.getStation(강남역.getId())).thenReturn(Optional.of(강남역));
    when(stationService.getStation(서면역.getId())).thenReturn(Optional.of(서면역));
    when(sectionService.findAll()).thenReturn(구간_목록);

    // when
    final var throwable = catchThrowable(() -> pathService.findPath(강남역.getId(), 서면역.getId()));

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("경로를 찾을 수 없습니다.");
  }

  @DisplayName("존재하지 않는 출발역")
  @Test
  void 존재하지_않는_출발역() {
    // given
    when(stationService.getStation(강남역.getId())).thenReturn(Optional.empty());

    // when
    final var throwable = catchThrowable(() -> pathService.findPath(강남역.getId(), 역삼역.getId()));

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("출발역 정보를 찾을 수 없습니다.");
  }

  @DisplayName("존재하지 않는 도착역")
  @Test
  void 존재하지_않는_도착역() {
    // given
    when(stationService.getStation(강남역.getId())).thenReturn(Optional.of(강남역));
    when(stationService.getStation(역삼역.getId())).thenReturn(Optional.empty());

    // when
    final var throwable = catchThrowable(() -> pathService.findPath(강남역.getId(), 역삼역.getId()));

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("출발역 정보를 찾을 수 없습니다.");
  }

  @DisplayName("출발역과 도착역이 같음")
  @Test
  void 출발역과_도착역이_같음() {
    // when
    final var throwable = catchThrowable(() -> pathService.findPath(강남역.getId(), 강남역.getId()));

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining( "출발역과 도착역이 같습니다.");
  }

  private static Section 구간을_생성(Station upStation, Station downStation, int distance) {
    return new Section(null, upStation, downStation, distance);
  }
}