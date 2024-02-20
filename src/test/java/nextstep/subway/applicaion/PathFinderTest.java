package nextstep.subway.applicaion;

import static com.navercorp.fixturemonkey.api.experimental.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PathFinderTest {

  static Station 강남역;
  static Station 역삼역;
  static Station 선릉역;
  static Station 한티역;
  static Station 양재역;
  static Station 매봉역;
  static Station 도곡역;
  static Station 남포역;
  static Station 서면역;
  static List<Section> 구간_목록;

  @InjectMocks
  private PathFinder pathFinder;

  @BeforeEach
  public void setUp() {
    /**
     * Given 여러 지하철 노선을 만든다.
     *                         3             1
     *                 강남 --------- 역삼 --------- 선릉
     *                  |                          |
     *                  |                          |   1
     *              1   |                         한티
     *                  |                          |
     *                  |                          |   1
     *                 양재 --------- 매봉 --------- 도곡
     *                         1            1
     *
     *                 남포 --------- 서면
     *                         5
     */

    강남역 = 지하철역_생성("강남역");
    역삼역 = 지하철역_생성("역삼역");
    선릉역 = 지하철역_생성("선릉역");
    한티역 = 지하철역_생성("한티역");
    양재역 = 지하철역_생성("양재역");
    매봉역 = 지하철역_생성("매봉역");
    도곡역 = 지하철역_생성("도곡역");
    남포역 = 지하철역_생성("남포역");
    서면역 = 지하철역_생성("서면역");

    구간_목록 = List.of(
        지하철_구간_생성(강남역, 역삼역, 3),
        지하철_구간_생성(역삼역, 선릉역, 1),
        지하철_구간_생성(강남역, 양재역, 1),
        지하철_구간_생성(선릉역, 한티역, 1),
        지하철_구간_생성(한티역, 도곡역, 1),
        지하철_구간_생성(양재역, 매봉역, 1),
        지하철_구간_생성(매봉역, 도곡역, 1),
        지하철_구간_생성(남포역, 서면역, 5)
    );
  }

  @DisplayName("최단 경로 조회 성공")
  @Test
  void 최단_경로_조회_성공() {
    // when
    final var result = pathFinder.find(구간_목록, 매봉역, 역삼역);

    // then
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getDistance()).isEqualTo(4);
    assertThat(
        result.get().getVertices().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList())
    ).containsExactly(
        매봉역.getId(),
        도곡역.getId(),
        한티역.getId(),
        선릉역.getId(),
        역삼역.getId()
    );
  }

  @DisplayName("연결할 수 없는 경로")
  @Test
  void 연결할_수_없는_경로() {
    // when
    final var result = pathFinder.find(구간_목록, 강남역, 서면역);

    // then
    assertThat(result.isPresent()).isFalse();
  }

  private static Station 지하철역_생성(String name) {
    return FixtureUtil.getBuilder(Station.class)
        .set(javaGetter(Station::getName), name)
        .sample();
  }

  private static Section 지하철_구간_생성(Station upStation, Station downStation, int distance) {
    return FixtureUtil.getBuilder(Section.class)
        .setNull(javaGetter(Section::getLine))
        .set(javaGetter(Section::getUpStation), upStation)
        .set(javaGetter(Section::getDownStation), downStation)
        .set(javaGetter(Section::getDistance), distance)
        .sample();
  }
}