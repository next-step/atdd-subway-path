package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.PathFinder;
import nextstep.subway.domain.line.ShortPath;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.ShortPathSameStationException;
import nextstep.subway.exception.StationNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {

    private static final Station GANGNAM_STATION = new Station("강남역");
    private static final Station SEOLLEUNG_STATION = new Station("선릉역");
    private static final Station SUWON_STATION = new Station("수원역");
    private static final Station NOWON_STATION = new Station("노원역");
    private static final Station DEARIM_STATION = new Station("대림역");

    private static final String SHINBUNDANG_LINE_NAME = "신분당선";
    private static final String SHINBUNDANG_LINE_COLOR = "bg-red-600";
    private static final String TWO_LINE_NAME = "2호선";
    private static final String TWO_LINE_COLOR = "bg-green-600";
    private static final String THREE_LINE_NAME = "3호선";
    private static final String TRHEE_LINE_COLOR = "bg-blue-600";

    private Line shinbundangLine;
    private Line twoLine;
    private Line threeLine;
    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        shinbundangLine = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 2);
        twoLine = new Line(TWO_LINE_NAME, TWO_LINE_COLOR, SEOLLEUNG_STATION, SUWON_STATION, 3);
        threeLine = new Line(THREE_LINE_NAME, TRHEE_LINE_COLOR, GANGNAM_STATION, NOWON_STATION, 5);
        threeLine.addSection(NOWON_STATION, SUWON_STATION, 3);
        pathFinder = new PathFinder(List.of(shinbundangLine, twoLine, threeLine));
    }

    @DisplayName("강남역에서 수원역으로 가는 경로 2가지중 선릉역을 경유한 최단거리 경로를 리턴해야한다.")
    @Test
    void gangname_move_suwon() {
        // given when
        ShortPath shortPath = pathFinder.findShortPath(GANGNAM_STATION, SUWON_STATION);

        // then
        assertThat(shortPath.getStations()).hasSize(3).containsExactly(GANGNAM_STATION, SEOLLEUNG_STATION, SUWON_STATION);
        assertThat(shortPath.getDistance()).isEqualTo(5);
    }

    @DisplayName("선릉역에서 수원역으로 가는 경로 1가지를 리턴해야한다.")
    @Test
    void seolleung_move_suwon() {
        // given when
        ShortPath shortPath = pathFinder.findShortPath(SEOLLEUNG_STATION, SUWON_STATION);

        // then
        assertThat(shortPath.getStations()).hasSize(2).containsExactly(SEOLLEUNG_STATION, SUWON_STATION);
        assertThat(shortPath.getDistance()).isEqualTo(3);
    }

    @DisplayName("최단경로 조회 역중 노선에 포함되지 않은 역이 존재할 경우 에러를 던진다.")
    @Test
    void not_exist_station_in_line() {
        // given when then
        assertThatThrownBy(() -> pathFinder.findShortPath(SEOLLEUNG_STATION, DEARIM_STATION))
                .isExactlyInstanceOf(StationNotExistException.class)
                .hasMessage("노선에 역이 존재하지 않습니다.");
    }

    @DisplayName("최단경로 조회 시작역, 종착역이 동일할 경우 에러를 던진다.")
    @Test
    void shortpath_station_same() {
        // given when then
        assertThatThrownBy(() -> pathFinder.findShortPath(DEARIM_STATION, DEARIM_STATION))
                .isExactlyInstanceOf(ShortPathSameStationException.class)
                .hasMessage("최단경로 시작역, 종착역이 동일할 수 없습니다.");
    }

}