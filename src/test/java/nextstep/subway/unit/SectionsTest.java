package nextstep.subway.unit;

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
        건대입구역 = new Station("건대입구역");
        잠실역 = new Station("잠실역");
        성수역 = new Station("성수역");
        이호선.generateSection(10, 건대입구역, 잠실역);
    }

    @DisplayName("각 구간의 하행역과 다음 구간의 상행역이 연결되도록 조회한다.")
    @Test
    void getSections() {
        //given
        Station 용산역 = new Station("용산역");
        이호선.generateSection(9, 건대입구역, 용산역);
        이호선.generateSection(8, 건대입구역, 성수역);

        //when
        Sections result = 이호선.getSections();

        //then
        List<Station> upStations = result
                .getSections()
                .stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        assertThat(upStations).containsExactly(건대입구역, 성수역, 용산역);
    }

    @DisplayName("구간을 노선의 가운데 추가할 경우 기존에 연결되어있던 구간의 상행역이 등록할 구간의 하행역으로 변경된다.")
    @Test
    void changePreviousUpStation() {
        //when
        이호선.generateSection(9, 건대입구역, 성수역);

        //then
        assertThat(이호선.getStations()).containsExactly(건대입구역, 성수역, 잠실역);
    }

    @DisplayName("중간에 구간을 등록할 경우 등록할 구간의 거리가 기존 구간의 거리보다 긴 경우 예외가 발생한다.")
    @Test
    void changeUpStationDistanceException() {
        //then
        assertThatThrownBy(() -> 이호선.generateSection(20, 건대입구역, 성수역))
                .isExactlyInstanceOf(SectionException.class)
                .hasMessage("기존구간의 거리보다 더 길수 없습니다.");
    }
}
