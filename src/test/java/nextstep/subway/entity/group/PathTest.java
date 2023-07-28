package nextstep.subway.entity.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.fixture.unit.entity.StationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

class PathTest {
    private static final Line line = BDDMockito.mock(Line.class);

    @Test
    @DisplayName("출발역에서 목적지까지 최적 경로 찾기")
    void getPaths() {

        //given
        Station 부평역 = StationFixture.of(1);
        Station 구로역 = StationFixture.of(2);
        Station 신도림역 = StationFixture.of(3);
        Station 영등포구청역 = StationFixture.of(4);
        Station 특급역 = StationFixture.of(5);

        List<Section> sections = List.of(
            new Section(line, 부평역, 구로역, 5),
            new Section(line, 구로역, 신도림역, 5),
            new Section(line, 신도림역, 영등포구청역, 10),
            new Section(line, 구로역, 특급역, 5),
            new Section(line, 특급역, 영등포구청역, 5)
        );

        // 부평 - 5 - 구로 - 5 - 신도림 *** 10 *** 영등포구청
        //            | --- 5 --- 특급역 --- 5 --- |

        //given
        Path path = new Path(sections, 부평역, 영등포구청역);

        //when
        List<Station> result = path.getPath();

        //then
        assertThat(result).containsExactly(부평역, 구로역, 특급역, 영등포구청역);
    }

    @Test
    @DisplayName("출발역에서 목적지까지 최적 경로 가중치 추출")
    void getPathsWeight() {

        //given
        Station 부평역 = StationFixture.of(1);
        Station 구로역 = StationFixture.of(2);
        Station 신도림역 = StationFixture.of(3);
        Station 영등포구청역 = StationFixture.of(4);
        Station 특급역 = StationFixture.of(5);

        List<Section> sections = List.of(
            new Section(line, 부평역, 구로역, 5),
            new Section(line, 구로역, 신도림역, 5),
            new Section(line, 신도림역, 영등포구청역, 10),
            new Section(line, 구로역, 특급역, 5),
            new Section(line, 특급역, 영등포구청역, 5)
        );

        // 부평 - 5 - 구로 - 5 - 신도림 *** 10 *** 영등포구청
        //            | --- 5 --- 특급역 --- 5 --- |

        //given
        Path path = new Path(sections, 부평역, 영등포구청역);

        //when
        int distance = path.getPathDistance();

        //then
        assertThat(distance).isEqualTo(15);
    }

    @Test
    @DisplayName("출발역에서 목적지까지 최적 경로 가중치 추출")
    void getPathsWeight_notConnected() {

        //given
        Station 부평역 = StationFixture.of(1);
        Station 구로역 = StationFixture.of(2);
        Station 신도림역 = StationFixture.of(3);
        Station 영등포구청역 = StationFixture.of(4);

        List<Section> sections = List.of(
            new Section(line, 부평역, 구로역, 5),
            new Section(line, 신도림역, 영등포구청역, 10)
        );

        // 부평 - 5 - 구로 - 5 | 신도림 *** 10 *** 영등포구청

        //when - then
        assertThatThrownBy(() -> new Path(sections, 부평역, 영등포구청역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("출발지와 목적지의 각 구간이 이어져있지 않습니다.");

    }
}