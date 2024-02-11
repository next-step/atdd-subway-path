package nextstep.subway.domain;

import nextstep.subway.exception.ApplicationException;
import nextstep.subway.strategy.Dijkstra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class PathTest {

    private Path path;

    private Station 강남역;
    private Station 선릉역;
    private Station 양재역;
    private Station 역삼역;
    private Station 신대방역;

    @BeforeEach
    void setUp() {
        path = new Path(new Dijkstra());
        강남역 = GANGNAM_STATION.toStation(1L);
        선릉역 = SEOLLEUNG_STATION.toStation(2L);
        양재역 = YANGJAE_STATION.toStation(3L);
        역삼역 = YEOKSAM_STATION.toStation(4L);
        신대방역 = SINDAEBANG_STATION.toStation(5L);
    }

    @Test
    void 실패_출발역과_도착역이_같은_경우_경로를_조회할_수_없다() {
        assertThatThrownBy(() -> path.validatePath(강남역, 강남역))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("출발역과 도착역이 같은 경우 경로를 조회할 수 없습니다.");
    }

    @Test
    void 실패_출발역과_도착역이_연결되어_있지_않은_경우_경로를_조회할_수_없다() {
        assertThatThrownBy(() -> path.validatePath(강남역, 신대방역))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("출발역과 도착역이 연결되어 있지 않습니다.");
    }

    @Test
    void 실패_출발역이나_도착역이_노선에_쫀재하지_않을_경우_경로를_조회할_수_없다() {
        assertThatThrownBy(() -> path.validatePath(강남역, 신대방역))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("노선에 존재하지 않는 지하철역입니다.");
    }

    @Test
    void 성공_출발역과_도착역이_연결되어_있을_경우_최단_경로에_속하는_지하철역을_조회할_수_있다() {
        List<Station> stations = path.findShortenStations();
        assertThat(path.calculateShortenDistance()).isEqualTo(0);
    }

    @Test
    void 성공_출발역과_도착역이_연결되어_있을_경우_최단_경로의_거리를_조회할_수_있다() {
        assertThat(path.calculateShortenDistance()).isEqualTo(0);
    }

}
