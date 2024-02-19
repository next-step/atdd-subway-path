package nextstep.subway.unit.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.exception.SectionException;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class SectionsTest {

    private Line 이호선;
    Station 건대입구역;
    Station 잠실역;
    Station 성수역;

    @BeforeEach
    void setUp() {
        이호선 = new Line("2호선", "green");
        건대입구역 = new Station(1L, "건대입구역");
        잠실역 = new Station(2L, "잠실역");
        성수역 = new Station(3L, "성수역");
        이호선.generateSection(10, 건대입구역, 잠실역);
    }

    @DisplayName("각 구간의 하행역과 다음 구간의 상행역이 연결되도록 조회한다.")
    @Test
    void getSections() {
        //given
        Station 용산역 = new Station(4L, "용산역");
        이호선.generateSection(8, 잠실역, 용산역);

        //when
        Sections result = 이호선.getSections();

        //then
        List<Station> upStations = result.getStations();
        assertThat(upStations).containsExactly(건대입구역, 잠실역, 용산역);
    }

    @DisplayName("구간을 노선의 가운데 추가할 경우 기존에 연결되어있던 구간의 상행역이 등록할 구간의 하행역으로 변경된다.")
    @Test
    void changePreviousUpStation() {
        //when
        이호선.generateSection(9, 건대입구역, 성수역);

        //then
        assertThat(이호선.getStations()).containsExactly(건대입구역, 성수역, 잠실역);
        List<Integer> distances = getDistances();
        assertThat(distances).containsExactly(9, 1);
    }

    @DisplayName("중간에 구간을 등록할 경우 등록할 구간의 거리가 기존 구간의 거리보다 긴 경우 예외가 발생한다.")
    @Test
    void changeUpStationDistanceException() {
        //then
        assertThatThrownBy(() -> 이호선.generateSection(20, 건대입구역, 성수역))
                .isExactlyInstanceOf(SectionException.class)
                .hasMessage("기존구간의 거리보다 더 길수 없습니다.");
    }

    @DisplayName("중간에 구간을 삭제하면 구간이 재배치 된다.")
    @Test
    void deleteMiddleSection() {
        //given
        Station 용산역 = new Station("용산역");
        이호선.generateSection(10, 잠실역, 성수역);
        이호선.generateSection(10, 성수역, 용산역);

        //when
        이호선.deleteSection(성수역);

        //then
        assertThat(이호선.getStations()).containsExactly(건대입구역, 잠실역, 용산역);
        assertThat(getDistances()).containsExactly(10, 20);
    }

    @DisplayName("상행종점역을 삭제하면 그 다음 역이 상행종점역이 된다.")
    @Test
    void deleteUpFinalStation() {
        //given
        이호선.generateSection(10, 잠실역, 성수역);

        //when
        이호선.deleteSection(건대입구역);

        //then
        assertThat(이호선.getStations()).containsExactly(잠실역, 성수역);
        assertThat(getDistances()).containsExactly(10);
    }

    @DisplayName("하행종점역을 삭제하면 그 이전 역이 하행종점역이 된다.")
    @Test
    void deleteDownFinalStation() {
        //given
        이호선.generateSection(10, 잠실역, 성수역);

        //when
        이호선.deleteSection(성수역);

        //then
        assertThat(이호선.getStations()).containsExactly(건대입구역, 잠실역);
        assertThat(getDistances()).containsExactly(10);
    }

    private List<Integer> getDistances() {
        return 이호선.getSectionList().stream().map(Section::getDistance).collect(Collectors.toList());
    }
}
