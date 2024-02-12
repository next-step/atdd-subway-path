package nextstep.subway.factory;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.domain.ShortestPathType.DIJKSTRA;
import static nextstep.subway.domain.ShortestPathType.FLOYD;
import static nextstep.subway.fixture.LineFixture.SHINBUNDANG_LINE;
import static nextstep.subway.fixture.StationFixture.GANGNAM_STATION;
import static nextstep.subway.fixture.StationFixture.SEOLLEUNG_STATION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShortestPathFactoryTest {

    private ShortestPathFactory 최단거리;

    @BeforeEach
    void setUp() {
        최단거리 = new ShortestPathFactory();
    }

    @Test
    void 실패_구간_정보가_없을경우_최단거리_전략을__구할_수_없다(){
        assertThatThrownBy(() -> 최단거리.generateStrategy(DIJKSTRA, new ArrayList<>()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("지하철 구간이 존재하지 않습니다.");
    }

    @Test
    void 실패_존재하지_않는_최단거리_전략일_경우_최단거리_전략을_구할_수_없다(){
        assertThatThrownBy(() -> 최단거리.generateStrategy(FLOYD, List.of(강남역_선릉역_구간())))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("존재하지 않는 알고리즘 최단 전략 입니다.");
    }

    @Test
    void 성공_최단거리를_구할_수_없다(){
        최단거리.generateStrategy(DIJKSTRA, List.of(강남역_선릉역_구간()));
    }

    private static Section 강남역_선릉역_구간() {
        Station 강남역 = GANGNAM_STATION.toStation(1L);
        Station 선릉역 = SEOLLEUNG_STATION.toStation(2L);
        Line 신분당선 = SHINBUNDANG_LINE.toLine(1L);
        Section 강남역_선릉역_구간 = new Section(
                신분당선,
                강남역,
                선릉역,
                10L
        );
        return 강남역_선릉역_구간;
    }

}
