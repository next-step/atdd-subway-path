package nextstep.subway.unit.fixture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.section.Section;

/**
 * 노선도
 *
 * <2호선>
 * 봉천역 --3-- 서울대입구역 --4-- 낙성대역 --5-- 사당역 -- 10 -- 교대역
 *
 * <4호선>
 * 사당역 --5-- 총신대입구역 --5-- 동작역
 *
 * <7호선>
 * 총신대입구역 --15-- 내방역 --16-- 고속터미널역
 *
 * <6호선>
 * 교대 --5-- 고속터미널역
 *
 *
 * <999호선> (연결 X)
 * _999_시작역 --5-- _999_종점역
 */
public class SubwayLineFixture {

  // 2 - 4 호선 겹침
  public Station 사당역;

  // 4 - 7 호선 겹침
  public Station 총신대입구역;

  // 6 - 2 호선 겹침
  public Station 교대역;

  // 6 - 7 호선 겹침
  public Station 고속터미널역;

  // 2호선
  public Line _2호선;
  public Station 봉천역;
  public Station 서울대입구역;
  public Station 낙성대역;

  // 4호선
  public Line _4호선;
  public Station 동작역;

  // 6호선
  public Line _6호선;

  // 7호선
  public Line _7호선;
  public Station 내방역;

  // 999호선 (다른 노선들과 연결 되어있지 않음)
  public Line _999호선;
  public Station _999_시작역;
  public Station _999_종점역;

  public List<Line> 전체_노선;
  public Map<String, Station> 전체_역;

  public SubwayLineFixture() {
    사당역 = new Station(20L, "사당역");
    총신대입구역 = new Station(21L, "총신대입구역");
    교대역 = new Station(22L, "교대역");
    고속터미널역 = new Station(11L, "고속터미널역");

    전체_역 = new HashMap<>();
    전체_노선 = List.of(
      _2호선_init(),
      _4호선_init(),
      _6호선_init(),
      _7호선_init(),
      _999호선_init()
    );
  }

  private Line _2호선_init() {
    봉천역 = new Station(1L, "봉천역");
    서울대입구역 = new Station(2L, "서울대입구역");
    낙성대역 = new Station(3L, "낙성대역");
    _2호선 = new Line("2호선", "#00ff00");
    _2호선.addSection(new Section(_2호선, 봉천역, 서울대입구역, 3));
    _2호선.addSection(new Section(_2호선, 서울대입구역, 낙성대역, 4));
    _2호선.addSection(new Section(_2호선, 낙성대역, 사당역, 5));
    _2호선.addSection(new Section(_2호선, 사당역, 교대역, 5));
    전체_역_목록에_추가(_2호선.getStations());
    return _2호선;
  }

  private Line _4호선_init() {
    동작역 = new Station(6L, "동작역");
    _4호선 = new Line("4호선", "#cfcfcf");
    _4호선.addSection(new Section(_4호선, 사당역, 총신대입구역, 5));
    _4호선.addSection(new Section(_4호선, 총신대입구역, 동작역, 5));
    전체_역_목록에_추가(_4호선.getStations());
    return _4호선;
  }

  private Line _6호선_init() {
    _6호선 = new Line("6호선", "#cfccff");
    _6호선.addSection(new Section(_6호선, 교대역, 고속터미널역, 5));
    전체_역_목록에_추가(_6호선.getStations());
    return _6호선;
  }

  private Line _7호선_init() {
    내방역 = new Station(10L, "내방역");

    _7호선 = new Line("7호선", "#ggffgg");
    _7호선.addSection(new Section(_7호선, 총신대입구역, 내방역, 15));
    _7호선.addSection(new Section(_7호선, 내방역, 고속터미널역, 16));
    전체_역_목록에_추가(_7호선.getStations());
    return _7호선;
  }

  private Line _999호선_init() {
    _999_시작역 = new Station(900L, "_999_시작역");
    _999_종점역 = new Station(901L, "_999_종점역");

    _999호선 = new Line("999호선", "#00000");
    _999호선.addSection(new Section(_999호선, _999_시작역, _999_종점역, 5));
    전체_역_목록에_추가(_999호선.getStations());
    return _999호선;
  }

  private void 전체_역_목록에_추가(List<Station> stations) {
    for (Station station : stations) {
      전체_역.put(station.getName(), station);
    }
  }
}
